/*
 * JBILLING CONFIDENTIAL
 * _____________________
 *
 * [2003] - [2012] Enterprise jBilling Software Ltd.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Enterprise jBilling Software.
 * The intellectual and technical concepts contained
 * herein are proprietary to Enterprise jBilling Software
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden.
 */

package jbilling

import com.sapienter.jbilling.client.util.Constants
import com.sapienter.jbilling.client.util.DownloadHelper
import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.server.customer.CustomerBL
import com.sapienter.jbilling.server.invoice.InvoiceBL
import com.sapienter.jbilling.server.invoice.db.InvoiceDAS
import com.sapienter.jbilling.server.item.CurrencyBL
import com.sapienter.jbilling.server.metafields.DataType
import com.sapienter.jbilling.server.metafields.db.MetaField
import com.sapienter.jbilling.server.metafields.db.value.IntegerMetaFieldValue
import com.sapienter.jbilling.server.metafields.db.value.StringMetaFieldValue
import com.sapienter.jbilling.server.item.db.PlanDTO
import com.sapienter.jbilling.server.item.TariffPlan
import com.sapienter.jbilling.server.order.OrderWS
import com.sapienter.jbilling.server.order.db.*
import com.sapienter.jbilling.server.order.validator.OrderHierarchyValidator
import com.sapienter.jbilling.server.security.Validator
import com.sapienter.jbilling.server.user.UserBL
import com.sapienter.jbilling.server.user.UserDTOEx
import com.sapienter.jbilling.server.user.UserWS
import com.sapienter.jbilling.server.user.partner.db.PartnerDTO
import com.sapienter.jbilling.server.user.db.CustomerDTO
import com.sapienter.jbilling.server.user.db.UserDAS
import com.sapienter.jbilling.server.user.db.UserDTO
import com.sapienter.jbilling.server.util.IWebServicesSessionBean;
import com.sapienter.jbilling.server.util.PreferenceBL
import com.sapienter.jbilling.server.util.SecurityValidator
import com.sapienter.jbilling.server.util.csv.CsvExporter
import com.sapienter.jbilling.server.util.csv.Exporter
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.order.db.OrderStatusDAS
import com.sapienter.jbilling.server.order.OrderStatusFlag
import com.sapienter.jbilling.client.util.SortableCriteria
import com.sapienter.jbilling.server.invoice.InvoiceWS
import com.sapienter.jbilling.server.metafields.MetaFieldBL
import com.sapienter.jbilling.server.metafields.EntityType
import com.sapienter.jbilling.client.metafield.MetaFieldBindHelper
import com.sapienter.jbilling.server.metafields.MetaFieldValueWS
import com.sapienter.jbilling.server.process.db.PeriodUnitDTO

import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Restrictions
import org.hibernate.criterion.DetachedCriteria
import org.hibernate.criterion.Property
import org.hibernate.criterion.Projections
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.annotation.Secured
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.Criteria
import org.hibernate.FetchMode


/**
 *
 * @author vikas bodani
 * @since  20-Jan-2011
 *
 */

@Secured(["MENU_92"])
class OrderController {
	static scope = "prototype"
    static pagination = [ max: 10, offset: 0, sort: 'id', order: 'desc' ]
    static versions = [ max: 25 ]

    // Matches the columns in the JQView grid with the corresponding field
    static final viewColumnsToFields =
            ['customer': 'u.userName',
             'company': 'company.description',
             'orderid': 'id',
             'date': 'createDate',
             'amount': 'total']

    IWebServicesSessionBean webServicesSession
    def viewUtils
    def filterService
    def recentItemService
    def breadcrumbService
    def subAccountService
    def springSecurityService
    def mediationProcessService
    def mediationService
    SecurityValidator securityValidator

    def auditBL

    def index () {
        list()
    }

    def getFilteredOrders(filters, params, ids) {
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset
        params.sort = params?.sort ?: pagination.sort
        params.order = params?.order ?: pagination.order

		def user_id = session['user_id']
		def partnerDtos = PartnerDTO.createCriteria().list(){
				eq('baseUser.id', session['user_id'])
		}
		log.debug "### partner:" + partnerDtos  
		
		def customersForUser = new ArrayList()
		if( partnerDtos.size > 0 ){
			customersForUser = 	CustomerDTO.createCriteria().list(){
				'in'('partner', partnerDtos)
			}   
		}
		
		log.debug "### customersForUser:" + customersForUser     
        def company_id = session['company_id']
        return OrderDTO.createCriteria().list(
                max:    params.max,
                offset: params.offset
        ) {
            createAlias('baseUserByUserId', 'u', Criteria.LEFT_JOIN)
            createAlias('u.company','company')
            and {
                filters.each { filter ->
                    if (filter.value) {
                        //handle orderStatus & orderPeriod separately
                        if (filter.constraintType == FilterConstraint.STATUS) {
                            if (filter.field == 'orderStatus') {
                                def statuses = new OrderStatusDAS().findAll()
                                eq("orderStatus", statuses.find{ it.id == filter.integerValue })
                            } else if (filter.field == 'orderPeriod') {
                                def periods = new OrderPeriodDAS().findAll()
                                eq("orderPeriod", periods.find{ it.id == filter.integerValue })
                            }
                        } else if (filter.field == 'contact.fields') {
                            String typeId = params['contact.fields.fieldKeyData']?params['contact.fields.fieldKeyData']:filter.fieldKeyData
                            String ccfValue = filter.stringValue;
                            log.debug "Contact Field Type ID: ${typeId}, CCF Value: ${ccfValue}"
                            
                            if (typeId && ccfValue) {
                                MetaField type = findMetaFieldType(typeId.toInteger());
                                if (type != null) {
                                    createAlias("metaFields", "fieldValue")
                                    createAlias("fieldValue.field", "type")
                                    setFetchMode("type", FetchMode.JOIN)
                                    eq("type.id", typeId.toInteger())

                                    switch (type.getDataType()) {
                                        case DataType.STRING:
                                        	def subCriteria = DetachedCriteria.forClass(StringMetaFieldValue.class, "stringValue")
                                        					.setProjection(Projections.property('id'))
										    				.add(Restrictions.like('stringValue.value', ccfValue + '%').ignoreCase())
     
                                        	addToCriteria(Property.forName("fieldValue.id").in(subCriteria))
                                            break;
                                        case DataType.INTEGER:
                                        	def subCriteria = DetachedCriteria.forClass(IntegerMetaFieldValue.class, "integerValue")
                                        					.setProjection(Projections.property('id'))
										    				.add(Restrictions.eq('integerValue.value', ccfValue.toInteger()))
     
                                        	addToCriteria(Property.forName("fieldValue.id").in(subCriteria))
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
                        } else if(filter.field == 'u.company.description') {
							eq('u.company', CompanyDTO.findByDescriptionIlike('%' + filter.stringValue + '%'))
                        } else if(filter.field == 'userCodes.userCode.identifier') {
                            createAlias("userCodeLinks", "userCodes")
                            createAlias("userCodes.userCode", "userCode")
                            addToCriteria( Restrictions.eq("userCode.identifier",  filter.stringValue) )
                        } else if (filter.field == 'orderProcesses.billingProcess.id') {
                             List<Integer> orderProcessIds = new OrderProcessDAS().findByBillingProcess(filter.integerValue)
                             if (orderProcessIds.size() > 0) {
                                 createAlias("orderProcesses", "op")
                                 'in'('op.id', orderProcessIds)
                             } else {
                                 'sqlRestriction'("(1=0)")
                             }
                        } else if(filter.field == 'tariff') {
                            boolean tariff = filter.stringValue?.equals(TariffPlan.TARIFF.name()) ? true : false

                            DetachedCriteria dc = DetachedCriteria.forClass(PlanDTO.class, "plan")
                            dc.add(Restrictions.eq("plan.tariff", tariff))
                            dc.createAlias("plan.item", "item")
                            dc.setProjection(Projections.property('item.id'))

                            createAlias("lines","lines")
                            createAlias("lines.item", "item")
                            addToCriteria(Property.forName("item.id").in(dc))
                        } else if (filter.field  == 'changeStatus' ) {
                            sqlRestriction(""" exists (select oc.order_id FROM order_change as oc WHERE oc.user_assigned_status_id=? AND oc.order_id={alias}.id) """, [filter.integerValue])
                        } else {
                            addToCriteria(filter.getRestrictions());
                        }
                    }
                }

                if(params.company) {
                    eq('u.company', CompanyDTO.findByDescriptionIlike('%' + params.company + '%'))
                }
                if(params.orderid) {
                    eq('id', params.int('orderid'))
                }
                if (params.customer) {
                    addToCriteria(Restrictions.ilike('u.userName', params.customer, MatchMode.ANYWHERE))
                }

				//all the orders related to this company and its childs
				'in'('u.company', retrieveCompanies())
                eq('deleted', 0)

                if (SpringSecurityUtils.ifNotGranted("ORDER_28")) {
                    UserDTO loggedInUser = UserDTO.get(springSecurityService.principal.id)

                    if (loggedInUser.getPartner() != null) {
                        // #7043 - Agents && Commissions - A logged in Partner should only see its orders and the ones of his children.
                        // A child Partner should only see its orders.
                        def partnerIds = []
                        if (loggedInUser.getPartner() != null) {
                            partnerIds << loggedInUser.partner.user.id
                            if (loggedInUser.partner.children) {
                                partnerIds += loggedInUser.partner.children.user.id
                            }
                        }
                        createAlias("baseUserByCreatedBy", "createdBy")
                        'in'('createdBy.id', partnerIds)
                    } else if (SpringSecurityUtils.ifAnyGranted("ORDER_29")) {
                        // restrict query to sub-account user-ids
                        'in'('u.id', subAccountService.subAccountUserIds)
                    } else {
                        // limit list to only this customer
                        or {
							'in'('u.id', customersForUser.baseUser.userId)
							eq('u.id', user_id)
                        }
                    }
                }
                if (ids) {
                    'in'('id', ids.toArray(new Integer[ids.size()]))
                }
            }

            // apply sorting
            SortableCriteria.sort(params, delegate)
        }
    }

    def childrenMap(orders) {
        if (!orders) return [:]
        def queryResults = OrderDTO.executeQuery(
               "select ord.parentOrder.id as id, count(*) as childCount from ${OrderDTO.class.getSimpleName()} ord " +
               " where ord.parentOrder.id in (:orderIds) and ord.deleted = 0 group by ord.parentOrder.id ",
                [orderIds: orders.collect { it.id }]
        )
        def results = [:];
        queryResults.each({ record -> results.put(record[0], record[1]) })
        return results;
    }

    def findOrders () {
        def filters = filterService.getFilters(FilterType.ORDER, params)

        def orderIds
        if (params.mediationId) {
            def mediationProcessWS = webServicesSession.getMediationProcess(UUID.fromString(params.get('mediationId')))
            orderIds = mediationProcessWS?.orderIds
        } else {
            orderIds = parameterIds
        }

        params.sort = viewColumnsToFields[params.sidx]
        params.order  = params.sord
        params.max = params.rows
        params.offset = params?.page ? (params.int('page')-1) * params.int('rows') : 0
        params.alias = SortableCriteria.NO_ALIAS

        def orders = getFilteredOrders(filters, params, orderIds)

        try {
            render getOrdersJsonData(orders, params) as JSON

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e)
            render e.getMessage()
        }

    }

    /**
     * Converts Orders to JSon
     */
    private def Object getOrdersJsonData(orders, GrailsParameterMap params) {
        def jsonCells = orders
        def currentPage = params.page ? Integer.valueOf(params.page) : 1
        def rowsNumber = params.rows ? Integer.valueOf(params.rows): 1
        def numberOfPages = Math.ceil(orders.totalCount / rowsNumber)

        def jsonData = [rows: jsonCells, page: currentPage, records: orders.totalCount, total: numberOfPages]

        jsonData
    }

    def list () {
        def filters = filterService.getFilters(FilterType.ORDER, params)

        def orderIds = parameterIds

        def selected = params.id ? webServicesSession.getOrder(params.int("id")) : null
        breadcrumbService.addBreadcrumb(controllerName, 'list', null, selected?.id)

        def usingJQGrid = PreferenceBL.getPreferenceValueAsBoolean(session['company_id'], Constants.PREFERENCE_USE_JQGRID);
        //If JQGrid is showing, the data will be retrieved when the template renders
        if (usingJQGrid){
            if (params.applyFilter || params.partial) {
                render template: 'ordersTemplate', model: [currencies: retrieveCurrencies(), filters: filters]
            }else {
                render view: 'list', model: [currencies: retrieveCurrencies(), filters: filters]
            }
            return
        }

        def orders = getFilteredOrders(filters, params, orderIds)
        def user = selected ? webServicesSession.getUserWS(selected.userId) : null
        def isCurrentCompanyOwning = selected ? user.entityId?.equals(session['company_id']) ? true : false : false

        // if the id exists and is valid and there is no record persisted for that id, write an error message
        if(params.int("id") > 0 && selected == null){
            flash.error = message(code: 'flash.order.not.found')
        }

        if (params.applyFilter || params.partial) {
            render template: 'ordersTemplate', model: [ orders: orders, order: selected, user: user, currencies: retrieveCurrencies(), 
								filters: filters, ids: params.ids, children: childrenMap(orders),
                                isCurrentCompanyOwning: isCurrentCompanyOwning ]
			
        } else {

			PeriodUnitDTO periodUnit = selected?.dueDateUnitId ? PeriodUnitDTO.get(selected.dueDateUnitId) : null
            render view: 'list', model: [ orders: orders, order: selected, user: user, currencies: retrieveCurrencies(), 
								filters: filters, ids: params.ids, periodUnit: periodUnit, children: childrenMap(orders),
                                isCurrentCompanyOwning: isCurrentCompanyOwning]
        }
    }

    private def getChildren (GrailsParameterMap params, boolean withPagination) {
        def parent = OrderDTO.get(params.int('id'))

        securityValidator.validateUserAndCompany(UserBL.getWS(new UserDTOEx(parent?.baseUserByUserId)), Validator.Type.VIEW)

        def paginationValues = [max: params.max]
        if (withPagination) {
            paginationValues.offset = params.offset
        }

        def children = OrderDTO.createCriteria().list(paginationValues) {
            and {
                createAlias('baseUserByUserId', 'u', Criteria.LEFT_JOIN)
                eq('parentOrder.id', params.int('id'))
                eq('deleted', 0)
                order("id", "desc")
                if(params.company) {
                    eq('u.company', CompanyDTO.findByDescriptionIlike('%' + params.company + '%'))
                }
                if(params.orderid) {
                    eq('id', params.int('orderid'))
                }
                if (params.customer) {
                    addToCriteria(Restrictions.ilike('u.userName', params.customer, MatchMode.ANYWHERE))
                }
            }
        }
        children
    }

    /**
     * Fetches a list of sub-orders for the given order id and renders the order list "_table.gsp" template.
     */
    def suborders (){
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset

        def parent = OrderDTO.get(params.int('id'))

        securityValidator.validateUserAndCompany(UserBL.getWS(new UserDTOEx(parent?.baseUserByUserId)), Validator.Type.VIEW)

        def usingJQGrid = PreferenceBL.getPreferenceValueAsBoolean(session['company_id'], Constants.PREFERENCE_USE_JQGRID);
        //If JQGrid is showing, the data will be retrieved when the template renders
        if (usingJQGrid){
            render template: 'ordersTemplate', model:[parent: parent]
            return
        }

        def children = getChildren(params, true)

        render template: 'ordersTemplate', model: [ orders: children, parent: parent, children: childrenMap(children) ]
    }

    /**
     * JQGrid will call this method to get the list as JSon data
     */
    def findSuborders (){
        params.sort = viewColumnsToFields[params.sidx]
        params.order = params.sord
        params.max = params.rows
        params.offset = params?.page ? (params.int('page') - 1) * params.int('rows') : 0

        def children = getChildren(params, true)

        try {
            render getOrdersJsonData(children, params) as JSON

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e)
            render e.getMessage()
        }
    }
	
    def getSelectedOrder(selected, orderIds) {
        def idFilter = new Filter(type: FilterType.ALL, constraintType: FilterConstraint.EQ,
                field: 'id', template: 'id', visible: true, integerValue: selected.id)
        getFilteredOrders([idFilter], params, orderIds)
    }

    @Secured(["ORDER_24"])
    def show () {
        OrderWS order = webServicesSession.getOrder(params.int('id'))
        UserWS user = webServicesSession.getUserWS(order.getUserId())

        def isCurrentCompanyOwning = user.entityId?.equals(session['company_id']) ? true : false

        recentItemService.addRecentItem(order.id, RecentItemType.ORDER)
        breadcrumbService.addBreadcrumb(controllerName, 'list', null, order.id)

		PeriodUnitDTO periodUnit = order.dueDateUnitId ? PeriodUnitDTO.get(order.dueDateUnitId) : null
        render template:'show', model: [
                order: order,
                user: user,
                currencies: retrieveCurrencies(),
                periodUnit: periodUnit,
                filterStatusId : params.int('filterStatusId'),
                singleOrder : params.boolean("singleOrder"),
                isCurrentCompanyOwning: isCurrentCompanyOwning
        ]
    }

    /**
     * Applies the set filters to the order list, and exports it as a CSV for download.
     */
    @Secured(["ORDER_25"])
    def csv () {
        def filters = filterService.getFilters(FilterType.ORDER, params)

        params.sort = viewColumnsToFields[params.sidx] != null ? viewColumnsToFields[params.sidx] : params.sort
        params.order = params.sord
        params.max = CsvExporter.MAX_RESULTS

        def orderIds = parameterIds
        def orders = getFilteredOrders(filters, params, orderIds)
        renderCsvFor(orders)
    }

    /**
     * Called from the suborders table
     */
    @Secured(["ORDER_25"])
    def subordersCsv (){
        // For when the csv is exported on JQGrid
        params.sort = viewColumnsToFields[params.sidx] != null ? viewColumnsToFields[params.sidx] : params.sort
        params.order  = (params.sord != null ? params.sord : params.order)
        params.max = CsvExporter.MAX_RESULTS
        def orders = getChildren(params, false)
        renderCsvFor(orders)
    }

    def renderCsvFor(orders) {
        if (orders.totalCount > CsvExporter.MAX_RESULTS) {
            flash.error = message(code: 'error.export.exceeds.maximum')
            flash.args = [ CsvExporter.MAX_RESULTS ]
            redirect action: 'list', id: params.id

        } else {
            DownloadHelper.setResponseHeader(response, "orders.csv")
            Exporter<OrderDTO> exporter = CsvExporter.createExporter(OrderDTO.class);
            render text: exporter.export(orders), contentType: "text/csv"
        }
    }

    /**
     * Convenience shortcut, this action shows all invoices for the given user id.
     */
    def user () {
        def filter = new Filter(type: FilterType.ORDER, constraintType: FilterConstraint.EQ, field: 'baseUserByUserId.id', template: 'id', visible: true, integerValue: params.int('id'))
        filterService.setFilter(FilterType.ORDER, filter)
        redirect action: 'list'
    }

    @Secured(["ORDER_23"])
    def generateInvoice () {
        log.debug "generateInvoice for order ${params.id}"

        def orderId = params.id?.toInteger()

        Integer invoiceID= null;
        try {
            invoiceID = webServicesSession.createInvoiceFromOrder(orderId, null)

        } catch (SessionInternalError e) {
            flash.error= 'order.error.generating.invoice'
            redirect action: 'list', params: [ id: params.id ]
            return
        }

        if ( null != invoiceID) {
            flash.message ='order.geninvoice.success'
            flash.args = [orderId]
            redirect controller: 'invoice', action: 'list', params: [id: invoiceID]

        } else {
            flash.error ='order.error.geninvoice.inactive'
            redirect action: 'list', params: [ id: params.id ]
        }
    }

    @Secured(["ORDER_23"])
    def applyToInvoice () {
        def userId = params.int('userId')
        def bl = new UserBL(userId)
        securityValidator.validateUserAndCompany(bl.getUserWS(), Validator.Type.EDIT)

        def invoices = getApplicableInvoices(userId)

        if (!invoices || invoices.size() == 0) {
            flash.error = 'order.error.invoices.not.found'
            flash.args = [params.userId]
            redirect (action: 'list', params: [ id: params.id ])
        }

        session.applyToInvoiceOrderId = params.int('id')
        [ invoices:invoices, currencies: retrieveCurrencies(), orderId: params.id ]
    }

    @Secured(["ORDER_23"])
    def apply () {
        def order =  new OrderDAS().find(params.int('id'))
        def statusId =  new OrderStatusDAS().getDefaultOrderStatusId(OrderStatusFlag.INVOICE,session["company_id"])
        if (!order.getOrderStatus().getId().equals(statusId)) {
            flash.error = 'order.error.status.not.active'
        }

        // invoice with meta fields
        def invoiceTemplate = new InvoiceWS()
        bindData(invoiceTemplate, params, 'invoice')

        def invoiceMetaFields = retrieveInvoiceMetaFields();
        def fieldsArray = MetaFieldBindHelper.bindMetaFields(invoiceMetaFields, params);
        invoiceTemplate.metaFields = fieldsArray.toArray(new MetaFieldValueWS[fieldsArray.size()])

        // apply invoice to order.
        try {
            def invoice = webServicesSession.applyOrderToInvoice(order.getId(), invoiceTemplate)
            if (!invoice) {
                flash.error = 'order.error.apply.invoice'
                render view: 'applyToInvoice', model: [ invoice: invoice, invoices: getApplicableInvoices(params.int('userId')), currencies:retrieveCurrencies(), availableMetaFields: invoiceMetaFields, fieldsArray: fieldsArray ]
                return
            }

            flash.message = 'order.succcessfully.applied.to.invoice'
            flash.args = [params.id, invoice]

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e);

            def invoice = webServicesSession.getInvoiceWS(params.int('invoice.id'))
            def invoices = getApplicableInvoices(params.int('userId'))
            render view: 'applyToInvoice', model: [ invoice: invoice, invoices: invoices, currencies:retrieveCurrencies(), availableMetaFields: invoiceMetaFields, fieldsArray: fieldsArray ]
            return
        }

        redirect action: 'list', params: [ id: params.id ]
    }

    def getApplicableInvoices(Integer userId) {

        CustomerDTO payingUser
        Integer _userId
        UserDTO user= new UserDAS().find(userId)
        if (user.getCustomer()?.getParent()) {
            payingUser= new CustomerBL(user.getCustomer().getId()).getInvoicableParent()
            _userId=payingUser.getBaseUser().getId()
        } else {
            _userId= user.getId()
        }
        InvoiceDAS das= new InvoiceDAS()
        List invoices =  new ArrayList()
        for (Iterator it= das.findAllApplicableInvoicesByUser(_userId ).iterator(); it.hasNext();) {
            invoices.add InvoiceBL.getWS(das.find (it.next()))
        }

        log.debug "Found ${invoices.size()} for user ${_userId}"

        invoices as List
    }

	def retrieveCompanies(){
		def parentCompany = CompanyDTO.get(session['company_id'])
		def childs = CompanyDTO.findAllByParent(parentCompany)
		childs.add(parentCompany)
		return childs;
	}

    def retrieveInvoiceMetaFields() {
        return MetaFieldBL.getAvailableFieldsList(session["company_id"], EntityType.INVOICE);
    }

    def retrieveCurrencies() {
		//in this controller we need only currencies objects with inUse=true without checking rates on date
        return new CurrencyBL().getCurrenciesWithoutRates(session['language_id'].toInteger(), session['company_id'].toInteger(),true)
    }

    def byProcess () {
        // limit by billing process
        def processFilter = new Filter(type: FilterType.ORDER, constraintType: FilterConstraint.EQ, field: 'orderProcesses.billingProcess.id', template: 'id', visible: true, integerValue: params.int('processId'))
        filterService.setFilter(FilterType.ORDER, processFilter)
        def filters = filterService.getFilters(FilterType.ORDER, params)
		
        def orders = getFilteredOrders(filters, params, null)
        log.debug("Found ${orders.size()} orders.")
        render view: 'list', model: [orders: orders, filters: filters, children: childrenMap(orders)]
    }
	
    def byMediation () {

        def mediationId = UUID.fromString(params.get('id'))
        def filters=filterService.getFilters(FilterType.ORDER, params)
        def orders=[]
        def orderIds = mediationService.getOrdersForMediationProcess(mediationId)
        if (orderIds) {
            orders = getFilteredOrders(filters, params, orderIds)
        } else {
            orders = new ArrayList<OrderDTO>()
        }
        log.debug("Found ${orders?.size()} orders.")
        if (params.applyFilter || params.partial) {
            render template: 'ordersTemplate', model: [orders: orders, filters: filters, ids: params.ids, children: childrenMap(orders),
                                                       mediationId: mediationId]
        } else {
            render view: 'list', model: [orders: orders, filters: filters, ids: params.ids, children: childrenMap(orders),
                                         mediationId:mediationId, filterAction: 'byMediation', filterId:mediationId]
        }
    }

    @Secured(["ORDER_22"])
    def deleteOrder () {
        String orderIds = webServicesSession.deleteOrder(params.int('id'))

        if(orderIds.equals(OrderHierarchyValidator.ERR_NON_LEAF_ORDER_DELETE)){
            flash.error = message('code':OrderHierarchyValidator.ERR_NON_LEAF_ORDER_DELETE.split(",").last())
        }else if(orderIds.equals(OrderHierarchyValidator.PRODUCT_DEPENDENCY_EXIST)){
            flash.error = message('code':OrderHierarchyValidator.PRODUCT_DEPENDENCY_EXIST.split(",").last())
        }else{
            flash.message = 'order.delete.success'
            flash.args = [orderIds]
        }

        redirect action: 'list'
    }
    
    def retrieveAvailableMetaFields() {
        return MetaFieldBL.getAvailableFieldsList(session["company_id"], EntityType.ORDER);
    }

    def findMetaFieldType(Integer metaFieldId) {
        for (MetaField field : retrieveAvailableMetaFields()) {
            if (field.id == metaFieldId) {
                return field;
            }
        }
        return null;
    }

    def getParameterIds() {

        // Grails bug when using lists with <g:remoteLink>
        // http://jira.grails.org/browse/GRAILS-8330
        // TODO (pai) remove workaround

        def parameterIds = new ArrayList<Integer>()
        def idParamList = params.list('ids')
        idParamList.each { idParam ->
            if (idParam?.isInteger()) {
                parameterIds.add(idParam.toInteger())
            }
        }
        if (parameterIds.isEmpty()) {
            String ids = params.ids
            if (ids) {
                ids = ids.replace('[', "").replace(']', "")
                String [] numbers = ids.split(", ")
                numbers.each { paramId ->
                    if (paramId?.isInteger()) {
                        parameterIds.add(paramId.toInteger());
                    }
                }
            }
        }

        return parameterIds;
    }

    @Secured(["ORDER_24"])
    def history (){
        def order = OrderDTO.get(params.int('id'))

        securityValidator.validateUserAndCompany(UserBL.getWS(new UserDTOEx(order?.baseUserByUserId)), Validator.Type.VIEW)

        def currentOrder = auditBL.getColumnValues(order)
        def orderVersions = auditBL.get(OrderDTO.class, order.getAuditKey(order.id), versions.max)
        def lines = auditBL.find(OrderLineDTO.class, getOrderLineSearchPrefix(order))

        def records = [
                [ name: 'order', id: order.id, current: currentOrder, versions: orderVersions ]
        ]

        render view: '/audit/history', model: [ records: records, historyid: order.id, lines: lines, linecontroller: 'order', lineaction: 'linehistory' ]
    }

    def getOrderLineSearchPrefix(order) {
        return "${order.user.company.id}-usr-${order.user.id}-ord-${order.id}-"
    }

    @Secured(["ORDER_24"])
    def linehistory (){
        def line = OrderLineDTO.get(params.int('id'))

        securityValidator.validateUserAndCompany(UserBL.getWS(new UserDTOEx(line?.orderDTO?.baseUserByUserId)), Validator.Type.VIEW)

        def currentLine = auditBL.getColumnValues(line)
        def lineVersions = auditBL.get(OrderLineDTO.class, line.getAuditKey(line.id), versions.max)

        def records = [
                [ name: 'line', id: line.id, current: currentLine, versions: lineVersions ]
        ]

        render view: '/audit/history', model: [ records: records, historyid: line.purchaseOrder.id ]
    }

    def retrieveCompanyStatuses (){
        return webServicesSession.getOrderChangeStatusesForCompany() as List
    }
}
