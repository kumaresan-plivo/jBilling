package jbilling

import com.sapienter.jbilling.client.metafield.MetaFieldBindHelper
import com.sapienter.jbilling.client.util.Constants
import com.sapienter.jbilling.client.util.DownloadHelper
import com.sapienter.jbilling.client.util.SortableCriteria
import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.common.Util
import com.sapienter.jbilling.server.customerEnrollment.BrokerCatalogCreator
import com.sapienter.jbilling.server.customerEnrollment.CustomerEnrollmentCommentWS
import com.sapienter.jbilling.server.customerEnrollment.CustomerEnrollmentStatus
import com.sapienter.jbilling.server.customerEnrollment.CustomerEnrollmentWS
import com.sapienter.jbilling.server.customerEnrollment.db.CustomerEnrollmentDTO
import com.sapienter.jbilling.server.fileProcessing.FileConstants
import com.sapienter.jbilling.server.metafields.DataType
import com.sapienter.jbilling.server.metafields.MetaFieldBL
import com.sapienter.jbilling.server.metafields.MetaFieldExternalHelper
import com.sapienter.jbilling.server.metafields.MetaFieldType
import com.sapienter.jbilling.server.metafields.MetaFieldValueWS
import com.sapienter.jbilling.server.metafields.db.MetaField
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue
import com.sapienter.jbilling.server.metafields.db.value.StringMetaFieldValue
import com.sapienter.jbilling.server.metafields.validation.EmailValidationRuleModel
import com.sapienter.jbilling.server.metafields.validation.RangeValidationRuleModel
import com.sapienter.jbilling.server.metafields.validation.RegExValidationRuleModel
import com.sapienter.jbilling.server.user.EntityBL
import com.sapienter.jbilling.server.user.UserBL
import com.sapienter.jbilling.server.user.db.AccountInformationTypeDTO
import com.sapienter.jbilling.server.user.db.AccountTypeDTO
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.user.db.UserDTO
import com.sapienter.jbilling.server.util.IWebServicesSessionBean
import com.sapienter.jbilling.server.util.PreferenceBL
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import groovy.sql.Sql
import org.apache.tika.Tika
import org.hibernate.Criteria
import org.hibernate.FetchMode
import org.hibernate.criterion.Criterion
import org.hibernate.criterion.DetachedCriteria
import org.hibernate.criterion.LogicalExpression
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Property
import org.hibernate.criterion.Restrictions

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Secured(["MENU_904"])
class CustomerEnrollmentController {

    static scope = "prototype"
    static pagination = [max: 10, offset: 0, sort: 'id', order: 'desc']
    static versions = [ max: 25 ]

    // Matches the columns in the JQView grid with the corresponding field
    static final viewColumnsToFields =
            ['userName': 'baseUser.userName',
             'company': 'company.description',
             'status': 'status',
             'accountType': 'accountType.description',
             'dateCreated':'createDatetime']

    IWebServicesSessionBean webServicesSession
    def viewUtils
    def filterService
    def recentItemService
    def breadcrumbService
    def groovyPageRenderer
    def dataSource

    def index () {
        list()
    }

    def list () {
        def filters = filterService.getFilters(FilterType.CUSTOMER_ENROLLMENT, params)

        CustomerEnrollmentWS selected = params.id ? webServicesSession.getCustomerEnrollment(params.int('id')):null
        selected = retrieveCompanies().id.contains(selected?.entityId) ? selected : null
        breadcrumbService.addBreadcrumb(controllerName, 'list', null, selected?.id)
        // if id is present and invoice not found, give an error message along with the list
        if (params.id?.isInteger() && selected == null) {
            flash.error = 'validation.error.company.hierarchy.invalid.customer.enrollment.id'
            flash.args = [params.id]
        }
        List<CustomerEnrollmentDTO> customerEnrollmentDTOList= getFilteredCustomerEnrollments(filters, params)
        def usingJQGrid = PreferenceBL.getPreferenceValueAsBoolean(session['company_id'], Constants.PREFERENCE_USE_JQGRID);
        //If JQGrid is showing, the data will be retrieved when the template renders
        AccountTypeDTO accountType=null
        if (selected){
            accountType=AccountTypeDTO.get(selected.accountTypeId)
        }
        if (params.applyFilter || params.partial) {
            render template: 'customerEnrollmentTemplate', model: [ selected: selected, customerEnrollments: customerEnrollmentDTOList,filters: filters, accountInformationTypes: accountType?.informationTypes]
        } else {
            render view: 'list', model: [selected: selected, customerEnrollments: customerEnrollmentDTOList,filters: filters, accountInformationTypes: accountType?.informationTypes]
        }
    }

    def getFilteredCustomerEnrollments(filters, params) {
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset
        params.sort = params?.sort ?: pagination.sort
        params.order = params?.order ?: pagination.order

        List<String> contactFilters = ['contact.email']
        def company_id = session['company_id']
        return CustomerEnrollmentDTO.createCriteria().list(
                max:    params.max,
                offset: params.offset

        ) {
            createAlias("company", "company")
            and {
                if(!Collections.disjoint(contactFilters,filters.findAll{it.value}*.field)) {
                    createAlias("accountType", "accountType")
                    setFetchMode("accountType", FetchMode.JOIN)
                    createAlias("metaFields", "mfv")
                    setFetchMode("mfv", FetchMode.JOIN)
                    createAlias("mfv.field", "metaField")
                    or{
                        filters.each { filter ->
                            if (filter.value && contactFilters.contains(filter.field)) {
                                DetachedCriteria metaFieldTypeSubCrit = DetachedCriteria.forClass(MetaField.class,"metaFieldType")
                                        .setProjection(Projections.property('id'))

                                Criterion metaFieldTypeCrit =Property.forName("metaField.id").in(metaFieldTypeSubCrit)
                                def subCriteria = DetachedCriteria.forClass(StringMetaFieldValue.class, "stringMFValue")
                                        .setProjection(Projections.property('id'))
                                subCriteria.add(Restrictions.ilike('stringMFValue.value', filter.stringValue, MatchMode.ANYWHERE))
                                Criterion aitMfv = Property.forName("mfv.id").in(subCriteria)
                                LogicalExpression aitMfvAndType = Restrictions.and(aitMfv,metaFieldTypeCrit)
                                addToCriteria(aitMfvAndType)

//
                            }
                        }
                    }
                }


                filters.each { filter ->
                    if (filter.value != null) {
                        if (filter.field == 'enrollmentStatus') {
                            CustomerEnrollmentStatus customerEnrollmentStatus=CustomerEnrollmentStatus.VALIDATED
                            if(filter.value=="PENDING"){
                                customerEnrollmentStatus=CustomerEnrollmentStatus.PENDING
                            }
                            if(filter.value=="VALIDATED"){
                                customerEnrollmentStatus=CustomerEnrollmentStatus.VALIDATED
                            }
                            if(filter.value=="REJECTED"){
                                customerEnrollmentStatus=CustomerEnrollmentStatus.REJECTED
                            }
                            if(filter.value=="ENROLLED"){
                                customerEnrollmentStatus=CustomerEnrollmentStatus.ENROLLED
                            }
                            eq("status", customerEnrollmentStatus)
                        } else if(filter.field == 'e.company.description') {
                            addToCriteria( Restrictions.ilike("company.description",  filter.stringValue, MatchMode.ANYWHERE) );
                        } else if (filter.field == 'accountTypeFields') {
                            String typeId = params['accountTypeFields.fieldKeyData']?params['accountTypeFields.fieldKeyData']:filter.fieldKeyData
                            String ccfValue = filter.stringValue
                            log.debug "Account Field Type ID: ${typeId}, CCF Value: ${ccfValue}"
                            if (typeId && ccfValue) {
                                MetaField type = findMetaFieldType(typeId.toInteger());
                                if (type != null) {
                                    createAlias("metaFields", "type")
                                    createAlias("type.field", "field")
                                    setFetchMode("type", FetchMode.JOIN)
                                    eq("field.id", typeId.toInteger())

                                    switch (type.getDataType()) {
                                        case DataType.STRING:
                                            def subCriteria = DetachedCriteria.forClass(StringMetaFieldValue.class, "stringValue")
                                                    .setProjection(Projections.property('id'))
                                                    .add(Restrictions.like('stringValue.value', ccfValue + '%').ignoreCase())

                                            addToCriteria(Property.forName("type.id").in(subCriteria))
                                            break;
                                        case DataType.ENUMERATION:
                                        case DataType.JSON_OBJECT:
                                            addToCriteria(Restrictions.ilike("fieldValue.value", ccfValue, MatchMode.ANYWHERE))
                                            break;
                                        default:
                                            addToCriteria(Restrictions.eq("fieldValue.value", ccfValue))
                                            break;
                                    }
                                }
                            }
                        }else if (filter.field.contains("contact")) {

                        } else {
                             addToCriteria(filter.getRestrictions());
                        }
                    }
                }
                if(params.enrollmentId){
                 eq("id", params.int('enrollmentId'))
                }
                'in'('company', retrieveCompanies())
                eq("deleted", 0)
            }
            // apply sorting
            resultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
            SortableCriteria.sort(params, delegate)
        }
    }

    @Secured("CUSTOMER_ENROLLMENT_910")
    def show () {
        if(request.isXhr()){
            CustomerEnrollmentWS customerEnrollment = webServicesSession.getCustomerEnrollment(params.int('id'))
            breadcrumbService.addBreadcrumb(controllerName, 'show', null, customerEnrollment.id)

            AccountTypeDTO accountType= AccountTypeDTO.findById(customerEnrollment.accountTypeId)
            render template:'show', model: [customerEnrollment: customerEnrollment, accountInformationTypes: accountType.informationTypes]
        }else{
            redirect(action: 'list', params: ["id":params.int('id')])
        }
    }

    @Secured("CUSTOMER_ENROLLMENT_911")
    def edit(){
        Integer companyId =  session['company_id']
        Integer id=params.int('id')
        Integer accountTypeId=params.int("accountTypeId")
        CustomerEnrollmentWS customerEnrollment = (id > 0) ? webServicesSession.getCustomerEnrollment(params.int('id')):new CustomerEnrollmentWS()
        Integer userId
        Integer parentEnrollmentId
        if(chainModel){
            customerEnrollment=chainModel.customerEnrollment
            userId=params.int("userId")
            parentEnrollmentId=params.int("parentEnrollmentId")

        }
        Integer selectedCompanyId=params.int("user.entityId")

        if(id>0){
            accountTypeId=customerEnrollment.accountTypeId
        }
        if(selectedCompanyId){
            customerEnrollment.entityId=selectedCompanyId
        }

        List<AccountTypeDTO> accountTypes = AccountTypeDTO.createCriteria().list() {
            eq('company.id', companyId)
            order('id', 'asc')
        };

        breadcrumbService.addBreadcrumb(controllerName, actionName, null, params.int('id'))
        if(accountTypeId || id>0){
            AccountTypeDTO accountType =AccountTypeDTO.get(accountTypeId);
            List<AccountInformationTypeDTO> infoTypes = accountType?.informationTypes?.sort { it.displayOrder }
            Map validationMessage=new HashMap()
            infoTypes.each{AccountInformationTypeDTO ait->
               ait?.getMetaFields().each{MetaField metaField->

                   if(metaField.dataType==DataType.INTEGER || metaField.dataType==DataType.DECIMAL ){
                       validationMessage[metaField.id]= [rules: ["number" :true] ]
                   }

                   if(metaField?.fieldUsage){
                       if(metaField?.fieldUsage==MetaFieldType.EMAIL){
                           validationMessage[metaField.id]= [rules: ["email" :true] ]
                       }
                   }

                   if(metaField.validationRule){
                       def validationRuleModel=metaField.validationRule.ruleType.validationRuleModel
                       if(validationRuleModel instanceof EmailValidationRuleModel){
                           validationMessage[metaField.id]= [rules:["email" :true], message:["email":metaField.validationRule.getErrorMessage(1)]]
                       }
                       if(validationRuleModel instanceof RangeValidationRuleModel){

                           validationMessage[metaField.id]= [rules:[minlength:"${metaField?.validationRule?.ruleType?.validationRuleModel?.getValidationMinRangeField(metaField?.validationRule)}", maxlength: "${metaField?.validationRule?.ruleType?.validationRuleModel?.getValidationMinRangeField(metaField?.validationRule)}"], message:[minlength:metaField.validationRule.getErrorMessage(1), maxlength:metaField.validationRule.getErrorMessage(1)]]
                       }
                       if(metaField.validationRule.ruleType.validationRuleModel instanceof RegExValidationRuleModel){
                           validationMessage[metaField.id]= [rules:["regex" :"${validationRuleModel.getValidationRegExField(metaField?.validationRule)}"], message:[regex:metaField.validationRule.getErrorMessage(1)?.replaceAll("&#44;", ",")]]
                       }
                   }
               }

            }

            render view: "edit", model:[accountInformationTypes:infoTypes, accountTypeId:accountTypeId, customerEnrollment:customerEnrollment, validationMessage: validationMessage, userId:userId, parentEnrollmentId:parentEnrollmentId]

        }else{
            render view: "list", model: [accountTypes: accountTypes, companies:retrieveCompanies()]
        }
    }

    @Secured("CUSTOMER_ENROLLMENT_911")
    def editBulk() {
        render view: "editBulk"
    }

    def uploadBulk() {
        def bulkEnrollmentFile = request.getFile('bulkEnrollmentFile')

        if (!bulkEnrollmentFile.empty) {
            String contentType = new Tika().detect(bulkEnrollmentFile.inputStream)

            if (contentType.equals("text/plain")) {
                try {
                    Path bulkInboundFolderPath = Paths.get(Util.getSysProp("base_dir"), FileConstants.CUSTOMER_ENROLLMENT_FOLDER, session['company_id'] as String, FileConstants.INBOUND_PATH)
                    if (!Files.exists(bulkInboundFolderPath)) {
                        Files.createDirectories(bulkInboundFolderPath)
                    }
                    bulkEnrollmentFile.transferTo(Paths.get(bulkInboundFolderPath.toString(), bulkEnrollmentFile.originalFilename as String).toFile())
                    flash.message = message(code: "file.upload.successfully")
                }
                catch (SessionInternalError e) {
                    viewUtils.resolveException(flash, session.locale, e);
                }
                catch (Exception e) {
                    log.error e.getMessage()
                    flash.error = 'customer.bulk.enrollment.upload.error'
                }

                redirect action: 'list'
            }
            else {
                render view: "editBulk"
                flash.error = 'customer.bulk.enrollment.upload.csvFile.invalidContentType'
            }
        }
        else {
            render view: "editBulk"
            flash.error = 'customer.bulk.enrollment.upload.csvFile.fileNotSelected'
        }
    }

    def downloadBulkResponses() {
        Path bulkOutboundFolderPath = Paths.get(Util.getSysProp("base_dir"), FileConstants.CUSTOMER_ENROLLMENT_FOLDER, session['company_id'] as String, FileConstants.OUTBOUND_PATH)

        byte[] zipData = this.zipResponseFiles(this.getCsvResponseFiles(bulkOutboundFolderPath.toFile(), params.brokerId as String))

        DownloadHelper.sendFile(response, "${params.brokerId ?:"all"}-responses.zip", "application/zip", zipData)

        redirect action: 'list'
    }

    private List<File> getCsvResponseFiles(File outboundFolder, String brokerId) {
        if (outboundFolder.exists() && outboundFolder.isDirectory()) {
            List<File> files = outboundFolder.listFiles().findAll { it.isFile() && it.name.toLowerCase().endsWith(".csv") }
            return brokerId.equals("") ? files : files.findAll { it.name.startsWith(brokerId) }
        }

        return Collections.emptyList()
    }

    private byte[] zipResponseFiles(List<File> csvResponseFiles) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(byteArrayOutputStream))

        try {
            ZipEntry zipEntry
            for (File csvResponseFile : csvResponseFiles) {
                zipEntry = new ZipEntry(csvResponseFile.name)
                zipEntry.setTime(csvResponseFile.lastModified())
                zipOutputStream.putNextEntry(zipEntry)
                zipOutputStream.write(csvResponseFile.bytes)
            }
        }
        finally {
            zipOutputStream.close()
        }

        return byteArrayOutputStream.toByteArray()
    }

    def downloadCatalog() {
        Integer companyId = session['company_id'] as Integer

        CompanyDTO companyDTO = new EntityBL(companyId).getEntity()

        File catalogFile = new BrokerCatalogCreator().create(companyId, companyDTO.getBrokerCatalogVersion())

        DownloadHelper.sendFile(response, catalogFile.name, "text/csv", catalogFile.bytes)
    }

    @Secured("CUSTOMER_ENROLLMENT_911")
    def save(){
        Integer accountTypeId=params.int("user.accountTypeId")
        Integer id=params.int("id")
        CustomerEnrollmentWS customerEnrollmentWS=id ? webServicesSession.getCustomerEnrollment(id):new CustomerEnrollmentWS();
        if(id == 0 || id==null){
            customerEnrollmentWS.setStatus(CustomerEnrollmentStatus.PENDING)
            customerEnrollmentWS.setAccountTypeId(accountTypeId)
            customerEnrollmentWS.setId(id)
        }

        bindData(customerEnrollmentWS, params)
        bindMetaFields(customerEnrollmentWS, params)
        customerEnrollmentWS.setCreateDatetime(new Date())

        if (params.comment){
            List<CustomerEnrollmentCommentWS> commentWSList=new ArrayList<CustomerEnrollmentCommentWS>();
            CustomerEnrollmentCommentWS commentWS=new CustomerEnrollmentCommentWS();
            commentWS.setUserId(session['user_id'])
            commentWS.setDateCreated(new Date())
            commentWS.setComment(params.comment)
            commentWSList.add(commentWS)
            if (commentWSList){
                customerEnrollmentWS.setCustomerEnrollmentComments(commentWSList.toArray(new CustomerEnrollmentCommentWS[commentWSList.size()]))
            }
        }
        if(params.submit=="Complete"){
          customerEnrollmentWS.setStatus(CustomerEnrollmentStatus.VALIDATED)
        }

        try{
            Integer customerEnrollmentId=webServicesSession.createUpdateEnrollment(customerEnrollmentWS)
            if(params.submit!="Complete"){
                if(id>0){
                    flash.message=message(code: "customer.enrollment.updated.successfully", args: [customerEnrollmentId])
                }else{
                    flash.message= message(code: "customer.enrollment.created.successfully", args: [customerEnrollmentId])
                }
            }else{
                flash.message= message(code: "customer.enrollment.completed.successfully", args: [customerEnrollmentId])
            }
            redirect action: "list", params: [id:customerEnrollmentId]
            return
        } catch (SessionInternalError e){
            viewUtils.resolveException(flash, session.locale, e)
            chain action: 'edit', model: [customerEnrollment: customerEnrollmentWS], params: [accountTypeId:accountTypeId, userid:customerEnrollmentWS?.getParentUserId(), parentEnrollmentId:customerEnrollmentWS?.parentEnrollmentId]
            return
        }

    }

    def retrieveAvailableAitMetaFields(Integer accountType){
        return MetaFieldExternalHelper.getAvailableAccountTypeFieldsMap(accountType)
    }

    def bindMetaFields(customerEnrollmentWS, params) {

        List<MetaField> accountTypeMetaFields=new ArrayList<MetaField>()
        Map<Integer, List<MetaField>> aitMetaFields=retrieveAvailableAitMetaFields(customerEnrollmentWS.accountTypeId)
        aitMetaFields.each{key, value->
            accountTypeMetaFields.addAll(value)
        }
        List<MetaFieldValueWS> fieldsArray = MetaFieldBindHelper.bindMetaFieldsWithGroupId(accountTypeMetaFields, params);
        customerEnrollmentWS.metaFields = fieldsArray.toArray(new MetaFieldValueWS[fieldsArray.size()])
    }



    def validateEnrollment(){

        Map data=[:]

        Integer accountTypeId=params.int("user.accountTypeId")
        Integer id=params.int("id")
        Integer companyId=session['company_id']

        try{
            CustomerEnrollmentWS customerEnrollmentWS=id ? webServicesSession.getCustomerEnrollment(id):new CustomerEnrollmentWS();
            if(id == 0 || id==null){
                customerEnrollmentWS.setId(id)
                customerEnrollmentWS.setEntityId(companyId)
                customerEnrollmentWS.setAccountTypeId(accountTypeId)
            }

            bindData(customerEnrollmentWS, params)
            bindMetaFields(customerEnrollmentWS, params)
            customerEnrollmentWS.setCreateDatetime(new Date())

            customerEnrollmentWS=webServicesSession.validateCustomerEnrollment(customerEnrollmentWS)
            AccountTypeDTO accountType =AccountTypeDTO.get(accountTypeId);
            List<AccountInformationTypeDTO> infoTypes = accountType?.informationTypes?.sort { it.displayOrder }

            CustomerEnrollmentDTO dto=new CustomerEnrollmentDTO()
            MetaFieldBL.fillMetaFieldsFromWS(customerEnrollmentWS.getEntityId(), dto, customerEnrollmentWS.getMetaFields());
            if(customerEnrollmentWS.getMessage()){
                flash.info=customerEnrollmentWS.getMessage()
            }
            data.status="success"
            data.content="${g.render(template: '/customerEnrollment/edit', session:session, model: [accountInformationTypes:infoTypes, customerEnrollment:customerEnrollmentWS, enrollment:dto, session: session])}";
            data.content=data.content.replace("\n","");
            render data as JSON
        }catch (Exception e){
            viewUtils.resolveException(flash, session.locale, e)
            render data as JSON
        }
    }

    def retrieveCompanies(){
        def parentCompany = CompanyDTO.get(session['company_id'])
        def childs = CompanyDTO.findAllByParent(parentCompany)
        childs.add(parentCompany)
        return childs;
    }

    @Secured("CUSTOMER_ENROLLMENT_912")
    def delete(){
        Integer customerEnrollmentId=params.int("id")
        try{
            webServicesSession.deleteEnrollment(customerEnrollmentId)
            flash.message=message(code: "customer.enrollment.deleted", args: [customerEnrollmentId])
        }catch (Exception e){
            viewUtils.resolveException(flash, session.locale, e)
        }

        redirect(action: 'list')

    }

    def findMetaFieldType(Integer metaFieldId) {
               return MetaField.get(metaFieldId);
    }

    def searchCustomerOrEnrollment(){

        String blackListedMetaFieldName="Blacklisted"
        List<Integer> metaFieldIdList=params.list("metaFieldId")
        Map<MetaFieldType, String> metaFieldTypeValueMap=[:]
        Integer selectedCompanyId=params.int("entityId")
        CompanyDTO companyDTO =CompanyDTO.get(session['company_id'])
        Integer accountTypeId =params.int("user.accountTypeId")
        AccountTypeDTO accountTypeDTO=AccountTypeDTO.get(accountTypeId)
        Map result=[:]
        result.status="success"
        result.content=""
        metaFieldIdList.each{
            if(params."metaField_${it}.value"){
                if(MetaField.get(it).fieldUsage){
                    metaFieldTypeValueMap.put(MetaField.get(it).fieldUsage, params."metaField_${it}.value")
                }
            }
        }

        List<MetaField> metaFieldList=[]
        AccountTypeDTO parentAccountType=AccountTypeDTO.findAllByCompany(companyDTO.parent?companyDTO.parent:companyDTO).find{it.description==accountTypeDTO.description}
        parentAccountType.informationTypes.each{AccountInformationTypeDTO accountInformationTypeDTO->
            metaFieldList.addAll(accountInformationTypeDTO.metaFields)
        }
        Map<String, String> metaFieldNameValueMap=[:]

        metaFieldList.each{
            if(metaFieldTypeValueMap.keySet().contains(it.fieldUsage) && metaFieldTypeValueMap[it.fieldUsage]){
                String dataType=""
                def dataValue
                switch (it.dataType){
                    case DataType.STRING:
                        dataType='string_value';
                        dataValue = ""+metaFieldTypeValueMap[it.fieldUsage]
                        break;
                    case DataType.BOOLEAN :
                        dataType="boolean_value";
                        dataValue = metaFieldTypeValueMap[it.fieldUsage]?:false
                        break;
                    case DataType.INTEGER :
                        dataType="integer_value";
                        dataValue = metaFieldTypeValueMap[it.fieldUsage] as Integer
                        break;
                    case DataType.DECIMAL :
                        dataType="decimal_value";
                        dataValue = metaFieldTypeValueMap[it.fieldUsage] as float
                        break;
                    case DataType.ENUMERATION :
                        dataType='string_value';
                        dataValue = ""+metaFieldTypeValueMap[it.fieldUsage]
                        break;
                }

                metaFieldNameValueMap[it.name]=[data:dataValue, dataType: dataType]

            }
        }

        def db = new Sql(dataSource)
        String findCustomers=""
        List queryVariable=[]
        metaFieldNameValueMap.eachWithIndex {key, value, index ->
            findCustomers+="""(select act.customer_id as id from customer_account_info_type_timeline act, meta_field_value mfv, meta_field_name mfn where act.meta_field_value_id=mfv.id and mfv.meta_field_name_id=mfn.id and mfn.name='${key}' and mfv.${value.dataType} ilike ?) """
            Integer count=index+1;
            if(count < metaFieldNameValueMap.keySet().size()  ){
                findCustomers+=" INTERSECT "
            }
            queryVariable.add(value.data + "%")
        }

        if(findCustomers){
            List<Integer> customerIds = db.rows(findCustomers, queryVariable)?.id as List
            List<UserDTO> userDTOList=[]

            if(customerIds){
                userDTOList=UserDTO.createCriteria().list(){
                    createAlias("customer", "customer")
                    createAlias("customer.accountType", "accountType")
                    createAlias("company", "company")

                    and {
                        inList("customer.id", customerIds)
                        eq("accountType.id", parentAccountType.id)
                        eq("deleted", 0)
                        eq("company.id", companyDTO.parent?companyDTO.parent.id:companyDTO?.id)
                    }
                    resultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                }
            }


            userDTOList.each{UserDTO user->
                List<MetaFieldValue> values =  new ArrayList<MetaFieldValue>()
                values.addAll(user?.customer?.metaFields)
                new UserBL().getCustomerEffectiveAitMetaFieldValues(values, user?.customer?.getAitTimelineMetaFieldsMap())

                boolean isBlacklisted=user.customer.getMetaField(blackListedMetaFieldName, null) ?(Boolean)user.customer.getMetaField(blackListedMetaFieldName, null):false
                result.content+="${groovyPageRenderer.render(template: '/customerEnrollment/searchResult', model: [user:user, metaFields : values, entityId:selectedCompanyId, accountTypeId:accountTypeId, isBlacklisted:isBlacklisted])}"
            }
        }
        queryVariable=[]
        String findEnrollments=""
        metaFieldNameValueMap.eachWithIndex {key, value, index ->
            findEnrollments+="""(select cemf.customer_enrollment_id as id from  customer_enrollment_meta_field_map cemf, meta_field_value mfv, meta_field_name mfn where cemf.meta_field_value_id=mfv.id and mfv.meta_field_name_id=mfn.id and mfn.name='${key}' and mfv.${value.dataType} ilike ?)"""
            Integer count=index+1;
            if(count < metaFieldNameValueMap.keySet().size()  ){
                findEnrollments+=" INTERSECT "
            }
            queryVariable.add(value.data + '%')
        }

        if(findEnrollments){
            List<Integer> enrollmentIds = db.rows(findEnrollments, queryVariable)?.id as List
            List<AccountTypeDTO> accountTypeDTOList=[]
            CompanyDTO.findAllByParent(companyDTO.parent?:companyDTO).each{
                accountTypeDTOList.add(AccountTypeDTO.findAllByCompany(it).find{it.description==accountTypeDTO.description})
            }

            List<CustomerEnrollmentDTO> customerEnrollmentList=[]
            if(enrollmentIds){
                customerEnrollmentList=CustomerEnrollmentDTO.createCriteria().list(){
                    createAlias("company", "company")
                    createAlias("accountType", "accountType")
                    and{
                        inList('id', enrollmentIds)
                        eq("deleted", 0)
                        isNull('parentCustomer')
                        isNull('parentEnrollment')
                        isNull('user')
                        inList("accountType.id", accountTypeDTOList.id)
                        inList("company.id", CompanyDTO.findAllByParent(companyDTO.parent? companyDTO.parent:companyDTO).id)
                    }
                    resultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                }
            }
            customerEnrollmentList.each{CustomerEnrollmentDTO enrollmentDTO->
                result.content+="${groovyPageRenderer.render(template: '/customerEnrollment/searchResult', model: [enrollment:enrollmentDTO, metaFields : enrollmentDTO.metaFields, entityId:selectedCompanyId, accountTypeId:accountTypeId])}"
            }
        }
        render result as JSON
    }


    def updateEnrollmentForm(){

        CustomerEnrollmentWS customerEnrollmentWS=new CustomerEnrollmentWS()
        Integer userId=params.int("userId")
        Integer enrollmentId=params.int("enrollmentId")
        UserDTO user=UserDTO.get(userId)
        CustomerEnrollmentDTO customerEnrollmentDTO=enrollmentId?CustomerEnrollmentDTO.get(enrollmentId):null
        Integer entityId=params.int("entityId")

        Integer accountTypeId=params.int("accountTypeId")
        List<MetaFieldValueWS> metaFieldValueWSList=new ArrayList<MetaFieldValueWS>()
        Map<String, Set<MetaField>> metaFieldAndGroupMap=[:]
        AccountTypeDTO accountTypeDTO=AccountTypeDTO.get(accountTypeId)
        accountTypeDTO.informationTypes.each{AccountInformationTypeDTO accountInformationTypeDTO->
            metaFieldAndGroupMap.put(accountInformationTypeDTO.getName(), accountInformationTypeDTO.metaFields)
        }

        if (user){
            metaFieldAndGroupMap.each{String key, Set<MetaField> metaFields->
                metaFields.each { MetaField metaField ->
                    List<MetaFieldValue> metaFieldValueList=user.customer.customerAccountInfoTypeMetaFields.metaFieldValue;
                    MetaFieldValueWS metaFieldValueWS=createMetaFieldValue(metaFieldValueList, metaField, key);
                    if(metaFieldValueWS){
                        metaFieldValueWSList.add(metaFieldValueWS);
                    }
                }
            }
        }

        if (customerEnrollmentDTO){
            metaFieldAndGroupMap.each{String key, Set<MetaField> metaFields->

                metaFields.each{MetaField metaField->
                    List<MetaFieldValue> metaFieldValueList=customerEnrollmentDTO.metaFields;
                    MetaFieldValueWS metaFieldValueWS=createMetaFieldValue(metaFieldValueList, metaField, key)
                    if(metaFieldValueWS){
                        metaFieldValueWSList.add(metaFieldValueWS);
                    }
                }
            }
        }

        customerEnrollmentWS.setMetaFields(metaFieldValueWSList.toArray(new MetaFieldValueWS[metaFieldValueWSList.size()]))
        chain action: 'edit', model: [customerEnrollment: customerEnrollmentWS], params: [accountTypeId:accountTypeId, userId:userId, parentEnrollmentId:enrollmentId, 'user.entityId':entityId]
    }

    private static MetaFieldValueWS createMetaFieldValue(List<MetaFieldValue> metaFieldValueList, MetaField metaField, String key){
        MetaFieldValue metaFieldValue=metaFieldValueList.find{it?.field?.fieldUsage && metaField.fieldUsage && it?.field?.fieldUsage==metaField.fieldUsage && AccountInformationTypeDTO.findById(it.field.getMetaFieldGroups().first().id).name==key}
        if(metaFieldValue){
            return new MetaFieldValueWS(fieldName:metaField.name,groupId:metaField?.metaFieldGroups?.first()?.id, disabled:metaField.disabled, mandatory:metaField.mandatory, dataType:metaField.dataType, defaultValue:metaField?.defaultValue, displayOrder:metaField?.displayOrder, value: metaFieldValue.value)
        }
        return null;
    }
}
