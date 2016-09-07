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

import com.sapienter.jbilling.csrf.RequiresValidFormToken
import com.sapienter.jbilling.common.Util
import com.sapienter.jbilling.server.invoice.PaperInvoiceBatchBL
import com.sapienter.jbilling.server.invoice.db.InvoiceDTO
import com.sapienter.jbilling.server.security.Validator
import com.sapienter.jbilling.server.user.UserDTOEx
import com.sapienter.jbilling.server.payment.PaymentTransferWS
import com.sapienter.jbilling.server.util.IWebServicesSessionBean;
import com.sapienter.jbilling.server.util.PreferenceBL
import com.sapienter.jbilling.server.util.SecurityValidator
import grails.converters.JSON
import com.sapienter.jbilling.client.metafield.MetaFieldBindHelper
import com.sapienter.jbilling.client.util.DownloadHelper
import com.sapienter.jbilling.client.util.SortableCriteria
import com.sapienter.jbilling.common.CommonConstants;
import com.sapienter.jbilling.common.Constants
import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.server.invoice.InvoiceWS
import com.sapienter.jbilling.server.item.CurrencyBL
import com.sapienter.jbilling.server.metafields.MetaFieldBL
import com.sapienter.jbilling.server.metafields.MetaFieldValueWS
import com.sapienter.jbilling.server.metafields.DataType
import com.sapienter.jbilling.server.metafields.db.MetaField
import com.sapienter.jbilling.server.metafields.db.value.IntegerMetaFieldValue
import com.sapienter.jbilling.server.metafields.db.value.StringMetaFieldValue
import com.sapienter.jbilling.server.metafields.EntityType
import com.sapienter.jbilling.server.payment.PaymentBL
import com.sapienter.jbilling.server.payment.PaymentInformationBL;
import com.sapienter.jbilling.server.payment.PaymentInformationWS
import com.sapienter.jbilling.server.payment.PaymentWS
import com.sapienter.jbilling.server.payment.db.PaymentDAS
import com.sapienter.jbilling.server.payment.db.PaymentDTO
import com.sapienter.jbilling.server.payment.db.PaymentInformationDTO
import com.sapienter.jbilling.server.payment.db.PaymentMethodDAS
import com.sapienter.jbilling.server.payment.db.PaymentMethodTypeDTO
import com.sapienter.jbilling.server.user.UserWS
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.util.csv.CsvExporter
import com.sapienter.jbilling.server.util.csv.Exporter

import grails.plugin.springsecurity.annotation.Secured
import grails.plugin.springsecurity.SpringSecurityUtils
import org.apache.commons.io.IOUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import com.sapienter.jbilling.server.user.db.UserDTO
import com.sapienter.jbilling.server.user.db.AccountTypeDTO
import com.sapienter.jbilling.server.user.UserBL
import com.sapienter.jbilling.server.user.partner.db.PartnerDTO
import com.sapienter.jbilling.server.user.db.CustomerDTO
import com.sapienter.jbilling.client.user.UserHelper

import org.hibernate.FetchMode
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Restrictions
import org.hibernate.criterion.DetachedCriteria
import org.hibernate.criterion.Property
import org.hibernate.criterion.Projections
import org.hibernate.Criteria


/**
 * PaymentController 
 *
 * @author Brian Cowdery
 * @since 04/01/11
 */
@Secured(["MENU_93"])
class PaymentController {
	static scope = "prototype"
    static pagination = [ max: 10, offset: 0, sort: 'id', order: 'desc' ]
    static versions = [ max: 25 ]

    // Matches the columns in the JQView grid with the corresponding field
    static final viewColumnsToFields =
            ['userName': 'u.userName',
             'company': 'company.description',
             'paymentId': 'id',
             'date': 'paymentDate',
             'paymentOrRefund': 'isRefund',
             'amount':'amount',
             'method':'paymentMethod',
             'result':'paymentResult']

    IWebServicesSessionBean webServicesSession
    def webServicesValidationAdvice
    def viewUtils
    def filterService
    def recentItemService
    def breadcrumbService
    def subAccountService
    def auditBL
    SecurityValidator securityValidator


    def index () {
        list()
    }

    def getList(filters, params) {
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset
        params.sort = params?.sort ?: pagination.sort
        params.order = params?.order ?: pagination.order

		def company_id = session['company_id'] as Integer
		def user_id = session['user_id'] as Integer
		
		def partnerDto = PartnerDTO.findByBaseUser (UserDTO.get(user_id))
		log.debug "### partner:" + partnerDto  
		
		def customersForUser = new ArrayList()
		if( partnerDto ){
			customersForUser = 	CustomerDTO.createCriteria().list(){
					eq('partner.id', partnerDto.id )
			}   
		}  
        log.debug "### customersForUser: " + customersForUser 
		
        return PaymentDTO.createCriteria().list(
                max:    params.max,
                offset: params.offset
        ) {
            createAlias('baseUser', 'u')

            // create alias only if applying invoice filters to prevent duplicate results
            if (filters.find{ it.field.startsWith('i.') && it.value })
                createAlias('invoicesMap', 'i', Criteria.LEFT_JOIN)

            and {
                filters.each { filter ->
                    if (filter.value != null) {
                    	if (filter.field == 'contact.fields') {
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
                                        // todo: now searching as string only, search for other types is impossible
//                                            def fieldValue = type.createValue();
//                                            bindData(fieldValue, ['value': ccfValue])
//                                            addToCriteria(Restrictions.eq("fieldValue.value", fieldValue.getValue()))

                                            addToCriteria(Restrictions.eq("fieldValue.value", ccfValue))
                                            break;
                                    }

                                }
                            }
                        } else if(filter.field == 'u.company.description') {
							eq('u.company', CompanyDTO.findByDescriptionIlike('%' + filter.stringValue + '%'))
						}else {
                        	addToCriteria(filter.getRestrictions());
                        }
                    }
                }

                if(params.company) {
                    eq('u.company', CompanyDTO.findByDescriptionIlike('%' + params.company + '%'))
                }
                if(params.paymentId) {
                    eq('id', params.int('paymentId'))
                }
                if (params.userName) {
                    addToCriteria(Restrictions.ilike('u.userName', params.userName, MatchMode.ANYWHERE))
                }

				//payments of parent + childs
				'in'('u.company', retrieveCompanies())
                eq('deleted', 0)

                if (SpringSecurityUtils.ifNotGranted("PAYMENT_36")) {
                    if (SpringSecurityUtils.ifAnyGranted("PAYMENT_37")) {
                        // restrict query to sub-account user-ids
                        'in'('u.id',subAccountService.getSubAccountUserIds())
                    } else {
                        // limit list to only this customer
                        if(customersForUser.isEmpty()){
                            eq('u.id', user_id)
                        }else {
                            or {
                                eq('u.id', user_id)
                                'in'('u.id', customersForUser.baseUser.userId)
                            }
                        }
                    }
                }
            }

            // apply sorting
            SortableCriteria.sort(params, delegate)
        }
    }

    /**
     * Gets a list of payments and renders the the list page. If the "applyFilters" parameter is given,
     * the partial "_payments.gsp" template will be rendered instead of the complete payments list page.
     */
    def list () {
        
		def filters = filterService.getFilters(FilterType.PAYMENT, params)

        def contactFieldTypes = params['contactFieldTypes']

        def usingJQGrid = PreferenceBL.getPreferenceValueAsBoolean(session['company_id'], com.sapienter.jbilling.client.util.Constants.PREFERENCE_USE_JQGRID);
        //If JQGrid is showing, the data will be retrieved when the template renders
        if (usingJQGrid){
            if (params.applyFilter || params.partial) {
                render template: 'paymentsTemplate', model: [filters: filters, contactFieldTypes: contactFieldTypes]
            }else {
                render view: 'list', model: [filters: filters, contactFieldTypes: contactFieldTypes]
            }
            return
        }

        def selected = params.id ? PaymentDTO.get(params.int("id")) : null

        if (selected) {
            securityValidator.validateUserAndCompany(UserBL.getWS(new UserDTOEx(selected?.baseUser)), Validator.Type.VIEW)
		}
		
		breadcrumbService.addBreadcrumb(controllerName, 'list', null, selected?.id)
		
        def payments = selected ? getListWithSelected(selected) : getList(filters, params)

        // if the id exists and is valid and there is no record persisted for that id, write an error message
        if(params.id?.isInteger() && selected == null){
            flash.error = message(code: 'flash.payment.not.found')
        }

        if (params.applyFilter || params.partial) {
            render template: 'paymentsTemplate', model: [ payments: payments, selected: selected, filters: filters, contactFieldTypes: contactFieldTypes ]
        } else {
            render view: 'list', model: [ payments: payments, selected: selected, filters: filters ]
        }
    }

    def findPayments () {
        def filters = filterService.getFilters(FilterType.PAYMENT, params)

        params.sort = viewColumnsToFields[params.sidx]
        params.order  = params.sord
        params.max = params.rows
        params.offset = params?.page ? (params.int('page')-1) * params.int('rows') : 0
        params.alias = SortableCriteria.NO_ALIAS

        def selected = params.id ? PaymentDTO.get(params.int("id")) : null
        def payments = selected ? getListWithSelected(selected) : getList(filters, params)

        try {
            render getPaymentsJsonData(payments, params) as JSON

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e)
            render e.getMessage()
        }

    }

    /**
     * Converts Payments to JSon
     */
    private def Object getPaymentsJsonData(payments, GrailsParameterMap params) {
        def jsonCells = payments
        def currentPage = params.page ? Integer.valueOf(params.page) : 1
        def rowsNumber = params.rows ? Integer.valueOf(params.rows): 1
        def numberOfPages = Math.ceil(jsonCells.totalCount / rowsNumber)

        def jsonData = [rows: jsonCells, page: currentPage, records: jsonCells.totalCount, total: numberOfPages]

        jsonData
    }

    def getListWithSelected(selected) {
        def idFilter = new Filter(type: FilterType.ALL, constraintType: FilterConstraint.EQ,
                field: 'id', template: 'id', visible: true, integerValue: selected.id)
        getList([idFilter], params)
    }

    /**
     * Applies the set filters to the payment list, and exports it as a CSV for download.
     */
    @Secured(["PAYMENT_35"])
    def csv () {
        def filters = filterService.getFilters(FilterType.PAYMENT, params)

        params.sort = viewColumnsToFields[params.sidx] != null ? viewColumnsToFields[params.sidx] : params.sort
        params.order = params.sord
        params.max = CsvExporter.MAX_RESULTS

        def payments = getList(filters, params)

        if (payments.totalCount > CsvExporter.MAX_RESULTS) {
            flash.error = message(code: 'error.export.exceeds.maximum')
            flash.args = [ CsvExporter.MAX_RESULTS ]
            redirect action: 'list', id: params.id

        } else {
            DownloadHelper.setResponseHeader(response, "payments.csv")
            Exporter<PaymentDTO> exporter = CsvExporter.createExporter(PaymentDTO.class);
            render text: exporter.export(payments), contentType: "text/csv"
        }
    }

    /**
     * Downloads the payment details for the given paymentID
     */
    @Secured(["hasAnyRole('PAYMENT_34', 'PAYMENT_36')"])
    def downloadPayment () {
        // get paymentId and userId from params
        Integer paymentId = params.int('paymentId')
        Integer userId = params.int('userId')
        try {
            // limit user to to only this customer's payment
            if ( SpringSecurityUtils.ifAllGranted("ROLE_CUSTOMER")  
                && SpringSecurityUtils.ifNotGranted("PAYMENT_36")) {
                log.debug 'Is Customer, and PAYMENT_36 Role not granted.'
                if ( !userId.equals(session['user_id'])) {
                    log.error "Unauthorized access: User ${session['user_id']} trying to view User ${userId} 's Payment"
                    throw new SessionInternalError("Unauthorized.");
                }
            }
            
            PaymentDTO paymentDto= PaymentDTO.get(paymentId)
            securityValidator.validateUserAndCompany(UserBL.getWS(new UserDTOEx(paymentDto?.baseUser)), Validator.Type.VIEW)
            UserDTO user = UserDTO.get(userId)
            
            // retrieve the payment kept at specified location
            String baseDir= com.sapienter.jbilling.common.Util.getSysProp("base_dir")
            String separator= System.getProperty("file.separator")
            String fileName= "Payment-${paymentDto.getId()}"
            String pdfLocation = "${baseDir}notifications${separator}${user.getUserName()}${separator}"
            
            log.debug "Pdf Location: $pdfLocation"

            new File(pdfLocation).mkdir()
            File file = File.createTempFile(fileName, "pdf", new File(pdfLocation))

            byte[] pdfBytes = file.getBytes()
            DownloadHelper.sendFile(response, fileName, "application/pdf", pdfBytes)

        } catch (FileNotFoundException fnfe) {
            log.error("File Not Found Exception "+fnfe)
            flash.error = "payment.prompt.failure.downloadPdf.fileNotFound"
            redirect(action: 'list', params: [id: paymentId])
        } catch (Exception e) {
            log.error("Some Exception occured "+e)
            flash.error = "payment.prompt.failure.downloadPdf"
            redirect(action: 'list', params: [id: paymentId])
        }

    }

    /**
     * Show details of the selected payment.
     */
    @Secured(["PAYMENT_34"])
    def show () {
        def payment = PaymentDTO.get(params.int('id'))
        securityValidator.validateUserAndCompany(UserBL.getWS(new UserDTOEx(payment?.baseUser)), Validator.Type.VIEW)
        
        if (!payment) {
            log.debug "redirecting to list"
            redirect(action: 'list')
            return
        }
        recentItemService.addRecentItem(params.int('id'), RecentItemType.PAYMENT)
        breadcrumbService.addBreadcrumb(controllerName, 'list', params.template ?: null, params.int('id'))

        render template: 'show', model: [ selected: payment ]
    }

    /**
     * Convenience shortcut, this action shows all payments for the given user id.
     */
    def user () {
        def filter =  new Filter(type: FilterType.PAYMENT, constraintType: FilterConstraint.EQ, field: 'u.id', template: 'id', visible: true, integerValue: params.id)
        filterService.setFilter(FilterType.PAYMENT, filter)

        redirect (action: "list")
    }

    /**
     * Delete the given payment id
     */
    @Secured(["PAYMENT_32"])
    def delete () {
        if (params.id) {
            try {
                webServicesSession.deletePayment(params.int('id'))
                log.debug("Deleted payment ${params.id}.")
                flash.message = 'payment.deleted'
                flash.args = [params.id]
            } catch (SessionInternalError e) {
                viewUtils.resolveExceptionMessage(flash, session.local, e)
                params.applyFilter = false
                params.partial = true
                list()
                return
            }
        }

        // render the partial payments list
        params.applyFilter = true
		redirect action: 'list'
        
    }

    /**
     * Shows the payment link screen for the given payment ID showing a list of un-paid invoices
     * that the payment can be applied to.
     */
    @Secured(["PAYMENT_33"])
    def link () {
        def payment = webServicesSession.getPayment(params.int('id'))
        def user = webServicesSession.getUserWS(payment?.userId ?: params.int('userId'))
        def invoices = getUnpaidInvoices(user.userId)

		// collects on those payment instruments that are allowed on front end
		List<PaymentInformationWS> paymentInstruments
        def instrument
        try {
            instrument = webServicesSession.getUserPaymentInstrument(user.userId, session['company_id'] as Integer)
        } catch (SessionInternalError e) {
            paymentDataNotFoundErrorRedirect(e, 'validation.payment.data.not.found', [user.getId()])
        }
        // verify if certain method of payment is allowed and add corresponding
		if(instrument) {
			paymentInstruments = instrument?.getUserPaymentInstruments()
		}
		
		// collects only those payment method types that are allowed on front end
		def paymentMethodTypes = AccountTypeDTO.get(user.accountTypeId).paymentMethodTypes

        render view: 'link', model: [ payment: payment, user: user, invoices: invoices, currencies: retrieveCurrencies(), invoiceId: params.invoiceId, availableFields: retrieveAvailableMetaFields(), paymentMethods: paymentMethodTypes, paymentInstruments : paymentInstruments, accountTypeId : user.accountTypeId ]
    }

    /**
     * Applies a given payment ID to the given invoice ID.
     */
    @Secured(["PAYMENT_33"])
    @RequiresValidFormToken
    def applyPayment () {
        def payment = webServicesSession.getPayment(params.int('id'))

        if (payment && params.invoiceId) {
            try {
                log.debug("appling payment ${payment} to invoice ${params.invoiceId}")
                webServicesSession.createPaymentLink(params.int('invoiceId'), payment.id)

                flash.message = 'payment.link.success'
                flash.args = [ payment.id, params.invoiceId ]

            } catch (SessionInternalError e) {
                viewUtils.resolveException(flash, session.local, e)
                link()
                return
            }

        } else if (!payment) {
            flash.warn = 'payment.not.exists'
            flash.args = [payment.id, params.invoiceId]
        } else {
            flash.warn = 'invoice.not.selected'
            flash.args = [payment.id, params.invoiceId]
        }


        // show the list page
        def filters = filterService.getFilters(FilterType.PAYMENT, params)
        def payments = getList(filters, params)

        render view: 'list', model: [ payments: payments, filters: filters ]
    }

    /**
     * Un-links the given payment ID from the given invoice ID and re-renders
     * the "show payment" view panel.
     */
    @Secured(["PAYMENT_33"])
    def unlink () {
        try {
            webServicesSession.removePaymentLink(params.int('invoiceId'), params.int('id'))
            flash.message = "payment.unlink.success"

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e);

        } catch (Exception e) {
            log.error("Exception unlinking invoice.", e)
            flash.error = "error.invoice.unlink.payment"
        }

        redirect action: 'list', params: [id: params.id]
    }

    /**
     * Redirects to the user list and sets a flash message.
     */
    @Secured(["PAYMENT_30"])
    def create () {
        flash.info = 'payment.select.customer'
        redirect controller: 'customer', action: 'list'
    }

    /**
     * Gets the payment to be edited and shows the "edit.gsp" view. This edit action cannot be used
     * to create a new payment, as creation requires a wizard style flow where the user is selected first.
     */
    @Secured(["hasAnyRole('PAYMENT_30', 'PAYMENT_31')"])
    def edit () {
        def payment
        def user

        try {
            payment = params.id ? webServicesSession.getPayment(params.int('id')) : new PaymentWS()

            if (payment?.deleted==1) {
            	paymentNotFoundErrorRedirect(params.id)
            	return
            }

            if (params.id) {
                PaymentBL paymentBL = new PaymentBL(params.int("id"))
                if (paymentBL.ifRefunded()) {
                    flash.error = 'validation.error.update.refunded.payment'
                    flash.args = [params.id]
                    redirect controller: 'payment', action: 'list'
                    return
                }
            }

            user = webServicesSession.getUserWS(payment?.userId ?: params.int('userId'))

        } catch (SessionInternalError e) {
            log.error("Could not fetch WS object", e)
			paymentNotFoundErrorRedirect(params.id)
            return
        }

        def invoices = getUnpaidInvoices(user.userId)
		
		// collects on those payment instruments that are allowed on front end
		List<PaymentInformationWS> paymentInstruments
        def instrument
        try {
            instrument = webServicesSession.getUserPaymentInstrument(user.userId, session['company_id'] as Integer)
        } catch (SessionInternalError e) {
            paymentDataNotFoundErrorRedirect(e, 'validation.payment.data.not.found', [user.getId()])
        }
        // verify if certain method of payment is allowed and add corresponding
		if(instrument) {
			paymentInstruments = instrument?.getUserPaymentInstruments()
		}
		
		// collects only those payment method types that are allowed on front end
		def paymentMethodTypes = AccountTypeDTO.get(user.accountTypeId).paymentMethodTypes
		
		//set payment amount equal of user total owned
		def payOwned=false
		if(params.payOwned){
			def owned=UserBL.getBalance(user.userId)
			payment.setAmount(owned)
			invoices=[]
			params.invoiceId=null
			payOwned=true
		}

        breadcrumbService.addBreadcrumb(controllerName, actionName, null, params.int('id'))
		

        //send all the payments of the current user as well which are not normal payments with a balance greater than zero
        List<PaymentDTO> refundablePayments = new PaymentDAS().getRefundablePayments(user.getUserId())
        log.debug "invoices are ${invoices}"
        log.debug "payments are ${refundablePayments}"
		
		def isCreditCard = isPaymentMethodCreditCard(paymentInstruments, paymentMethodTypes)

        [ payment: payment, user: user, invoices: invoices, currencies: retrieveCurrencies(),
			paymentMethods: paymentMethodTypes, invoiceId: params.int('invoiceId'), 
			refundablePayments: refundablePayments, refundPaymentId: params.int('payment?.paymentId'), 
			availableFields: retrieveAvailableMetaFields(), payOwned:payOwned, paymentInstruments : paymentInstruments, accountTypeId : user.accountTypeId , isCreditCard : isCreditCard]
    }

    private void paymentNotFoundErrorRedirect(paymentId) {
    	flash.error = 'payment.not.found'
		flash.args = [ paymentId as String ]
		redirect controller: 'payment', action: 'list'
    }

    private void paymentDataNotFoundErrorRedirect(exception, errorString, args) {
        Exception ex = viewUtils.getRootCause(exception)
        if (ex) {
            viewUtils.resolveException(flash, session.local, ex)
        } else if (errorString && args) {
            flash.error = errorString
            flash.args = args
        }
        redirect controller: 'payment', action: 'list'
        return
    }

    def getUnpaidInvoices(Integer userId) {
        def invoiceIds = webServicesSession.getUnpaidInvoices(userId);

        List<InvoiceWS> invoices = new ArrayList<InvoiceWS>(invoiceIds.size());
        for (Integer id : invoiceIds)
            invoices.add(webServicesSession.getInvoiceWS(id))
        return invoices;
    }

    /**
     * Shows a summary of the created/edited payment to be confirmed before saving.
     */
    @Secured(["hasAnyRole('PAYMENT_30', 'PAYMENT_31')"])
    @RequiresValidFormToken
    def confirm () {
        def payment = new PaymentWS()
		bindPayment(payment, params)
        session['user_payment']= payment

		// bind payment instruments
		def instrumentUser = new UserWS()
		def accountTypeId = params.int("accountTypeId")

        // make sure the user still exists before
        def user
		def userId = payment?.userId ?: params.int('userId')
        try {
            user = webServicesSession.getUserWS(userId)
            instrumentUser.setEntityId(user.getEntityId()) //where does the user belong
        } catch (SessionInternalError e) {
            log.error("Could not fetch WS object", e)

            flash.error = 'customer.not.found'
            flash.args = [ params.id ]

            redirect controller: 'payment', action: 'list'
            return
        }
		
		Integer listSize = instrumentUser.getPaymentInstruments().size()
		List<PaymentInformationWS> allPayments = new ArrayList<PaymentInformationWS>(listSize)
		List<PaymentInformationWS> toProcess = new ArrayList<PaymentInformationWS>(listSize)
		
		// puts payment informations that will be used to process payment in payment object depending upon processing order
		// puts all the payment instruments in a different array to preserve in case an error occurs
		instrumentUser.setId(userId)
		
        def invoices = getUnpaidInvoices(user.userId)
		List<PaymentDTO> refundablePayments = new PaymentDAS().getRefundablePayments(user.getUserId())
        // validate before showing the confirmation page
        try {
            UserHelper.bindPaymentInformations(instrumentUser ,params.int("modelIndex"), params)
			// also set payment methods for newly entered payment instruments
			categorizePayments(allPayments, toProcess, payment, instrumentUser)
            webServicesValidationAdvice.validateObject(payment)

            if(payment.amountAsDecimal == BigDecimal.ZERO) {
                String [] errors = ["PaymentWS,amount,validation.error.payment.amount.cannot.be.zero"]
                    throw new SessionInternalError("Payment Amount Cannot Be Zero",
                        errors);
            }
			
			if(payment.getPaymentInstruments().size() < 1) {
				String [] errors = ["PaymentWS,paymentMethodId,validation.error.apply.without.method"]
					throw new SessionInternalError("At least one payment method must be entered",
						errors);
			}
			
			if(payment.isRefund) {
				if(null==payment.getPaymentId()) {
					String [] errors = [
						"PaymentWS,paymentId,validation.error.payment.linked.refund"
					]
					throw new SessionInternalError("Cannot apply a Refund without a linked Payment ID",errors);
				}
				if(!PaymentBL.validateRefund(payment)){
					String [] errors = [
						"PaymentWS,paymentId,validation.error.apply.without.payment.or.different.linked.payment.amount"
					]
					throw new SessionInternalError("Either refund payment was not linked to any payment or the refund amount is in-correct",
						errors);
				}
			}
        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.local, e)
            boolean isCheque = false
            if (payment.methodId == Constants.PAYMENT_METHOD_CHEQUE) {
                 isCheque = true
            }
			//There no invoices needed for a total owned payment  
			if(params.payOwned){
				invoices=null
				
			}
			
			def paymentMethodTypes = AccountTypeDTO.get(accountTypeId).paymentMethodTypes

            render view: 'edit', model: [ payment: payment, user: user, invoices: invoices, refundablePayments: refundablePayments,
				currencies: retrieveCurrencies(), paymentMethods: paymentMethodTypes, invoiceId: params.int('invoiceId'),
				availableFields: retrieveAvailableMetaFields(), isCheque: isCheque,payOwned: params.payOwned,
				paymentInstruments : allPayments, accountTypeId : accountTypeId ]
            return
        }
		
        // validation passed, render the confirmation page
        def processNow = params.processNow ? true : false
        [ payment: payment, user: user, invoices: invoices, currencies: retrieveCurrencies(), 
			processNow: processNow, invoiceId: params.invoiceId, availableFields: retrieveAvailableMetaFields(), 
			paymentMethods : AccountTypeDTO.get(accountTypeId).getPaymentMethodTypes(), paymentInstruments : toProcess]
    }

    /**
     * Validate and save payment.
     */
    @Secured(["hasAnyRole('PAYMENT_30', 'PAYMENT_31')"])
    @RequiresValidFormToken
    def save () {

        /* Reuse the same payment that was bound earlier during confirm */
        def payment = session['user_payment'];
        //new PaymentWS()
        //bindPayment(payment, params)

        def invoiceId = params.int('invoiceId')

        // save or update
        try {
            if (!payment.id || payment.id == 0) {
                if (SpringSecurityUtils.ifAllGranted("PAYMENT_30")) {
                    def processNow = params.boolean('processNow') && payment.methodId != Constants.PAYMENT_METHOD_CHEQUE

                    log.debug("creating payment ${payment} for invoice ${invoiceId}")

                    if (processNow) {
                        log.debug("processing payment in real time")

                        def authorization = webServicesSession.processPayment(payment, invoiceId)
                        payment.id = authorization.paymentId

                        if (authorization.result) {
                            flash.message = 'payment.successful'
                            flash.args = [ payment.id ]

                        } else {
							def autorizationMessage = authorization.responseMessage;
							if (autorizationMessage == null) {
								autorizationMessage = "Payment processor unavailable"
							}
                            flash.error = 'payment.failed'
                            flash.args = [ payment.id, autorizationMessage ]
                        }

                    } else {
                        log.debug("entering payment")
                        payment.id = webServicesSession.applyPayment(payment, invoiceId)

                        if (payment.id) {
                            flash.info = 'payment.successful'
                            flash.args = [ payment.id ]

                        } else {
                            flash.info = 'payment.entered.failed'
                            flash.args = [ payment.id ]
                        }
                    }

                } else {
                    render view: '/login/denied'
                    return
                }

            } else {
                if (SpringSecurityUtils.ifAllGranted("PAYMENT_31")) {
                    log.debug("saving changes to payment ${payment.id}")
                    webServicesSession.updatePayment(payment)

                    if (invoiceId) {
                        log.debug("appling payment ${payment} to invoice ${invoiceId}")
                        webServicesSession.createPaymentLink(invoiceId, payment.id)
                    }

                    flash.message = 'payment.updated'
                    flash.args = [ payment.id ]

                } else {
                    render view: '/login/denied'
                }
            }

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.local, e)

            def user = webServicesSession.getUserWS(payment.userId)
            def invoices = getUnpaidInvoices(user.userId)
            List<PaymentDTO> refundablePayments = new PaymentDAS().getRefundablePayments(user.getUserId())
			
			// collects only those payment method types that are allowed on front end
			def paymentMethodTypes = AccountTypeDTO.get(user.accountTypeId).paymentMethodTypes
			
            render view: 'edit', model: [ payment: payment, user: user, invoices: invoices, currencies: retrieveCurrencies(), 
											paymentMethods: paymentMethodTypes, invoiceId: params.int('invoiceId'), availableFields: retrieveAvailableMetaFields(), 
											refundablePayments: refundablePayments, refundPaymentId: params.int('payment?.paymentId'), 
											paymentInstruments : payment?.getPaymentInstruments(), accountTypeId : user.accountTypeId, processNow : params.processNow ? true : false]
			
			return
			
        } finally {
            session.removeAttribute("user_payment")
        }

        chain action: 'list', params: [ id: payment.id ]
    }

    /**
     * Notify about this payment.
     */
    def emailNotify () {

        def pymId= params.id.toInteger()
        try {
            def result= webServicesSession.notifyPaymentByEmail(pymId)
            if (result) {
                flash.info = 'payment.notification.sent'
                flash.args = [ pymId ]
            } else {
                flash.error = 'payment.notification.sent.fail'
                flash.args = [ pymId ]
            }
        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.local, e)
        }
        chain action: 'list', params: [ id: pymId]
    }
    
    def bindPayment(payment, params) {
        if(params.isRefund == 'on' || params.isRefund == '1') {
            params.payment.isRefund = 1
        } else {
            params.payment.isRefund = 0
        }
        bindData(payment, params, 'payment')

        bindMetaFields(payment, params)

        return payment
    }

    def retrieveCurrencies() {
        return new CurrencyBL().getCurrenciesWithoutRates(session['language_id'].toInteger(), session['company_id'].toInteger(),true)
    }
	
	def retrieveCompanies(){
		def parentCompany = CompanyDTO.get(session['company_id'])
		def childs = CompanyDTO.findAllByParent(parentCompany)
		childs.add(parentCompany)
		return childs;
	}

    def retrieveAvailableMetaFields() {
        return MetaFieldBL.getAvailableFieldsList(session['company_id'], EntityType.PAYMENT);
    }

    def bindMetaFields(paymentWS, params) {
        def fieldsArray = MetaFieldBindHelper.bindMetaFields(retrieveAvailableMetaFields(), params);
        paymentWS.metaFields = fieldsArray.toArray(new MetaFieldValueWS[fieldsArray.size()])
    }

    def findMetaFieldType(Integer metaFieldId) {
        for (MetaField field : retrieveAvailableMetaFields()) {
            if (field.id == metaFieldId) {
                return field;
            }
        }
        return null;
    }

    @Secured(["PAYMENT_34"])
    def history (){
        def payment = PaymentDTO.get(params.int('id'))

        securityValidator.validateUserAndCompany(UserBL.getWS(new UserDTOEx(payment?.baseUser)), Validator.Type.VIEW)

        def currentPayment = auditBL.getColumnValues(payment)
        def paymentVersions = auditBL.get(PaymentDTO.class, payment.getAuditKey(payment.id), versions.max)

        def records = [
                [ name: 'payment', id: payment.id, current: currentPayment, versions: paymentVersions ],
        ]

        render view: '/audit/history', model: [ records: records, historyid: payment.id ]
    }

    def restore (){
        switch (params.record) {
            case "payment":
                def payment = PaymentDTO.get(params.int('id'));

                securityValidator.validateUserAndCompany(UserBL.getWS(new UserDTOEx(payment?.baseUser)), Validator.Type.EDIT)

                auditBL.restore(payment, payment.id, params.long('timestamp'))

                break;
        }

        chain action: 'history', params: [ id: params.historyid ]
    }

    private void validatePaymentInstrumentMetaFields(PaymentInformationDTO instrument, Integer entityId) {
    	instrument.updatePaymentMethodMetaFieldsWithValidation(entityId, instrument)
	}

    private void categorizePayments(
            List<PaymentInformationWS> allPayments, List<PaymentInformationWS> toProcess,
            PaymentWS payment, UserWS instrumentUser) {

        PaymentMethodDAS pmDas = new PaymentMethodDAS()
		PaymentInformationBL piBl = new PaymentInformationBL()
		PaymentInformationDTO instrument = null;
		for(PaymentInformationWS instrumentWS : instrumentUser.getPaymentInstruments()) {
			
			allPayments.add(instrumentWS)
			if(instrumentWS.getProcessingOrder() != null && instrumentWS.getProcessingOrder() != 0) {
				toProcess.add(instrumentWS)
				instrument = new PaymentInformationDTO(instrumentWS, session['company_id'])
				// set payment method if there is none defined
				if(instrumentWS.getPaymentMethodId() == null || instrumentWS.getPaymentMethodId() == 0) {
					instrumentWS.setPaymentMethodId(piBl.getPaymentMethodForPaymentMethodType(instrument))
				}
				validatePaymentInstrumentMetaFields(instrument, instrumentUser.entityId)
				payment.getPaymentInstruments().add(instrumentWS)
			}
		}
		
		// sort payment instruments with respect to processing order
		Collections.sort(payment.getPaymentInstruments(), PaymentInformationWS.ProcessingOrderComparator)
	}

    def addPaymentInstrument (){
        def user = new UserWS()
        UserHelper.bindPaymentInformations(user ,params.int("modelIndex"), params)

        def accountType = AccountTypeDTO.get(params.int("accountTypeId"))

        securityValidator.validateCompany(accountType?.company?.id, Validator.Type.EDIT)

        // show only recurring payment methods
        def paymentMethods = accountType?.paymentMethodTypes
        // add a new payment instrument
        PaymentInformationWS paymentInstrument = new PaymentInformationWS()
        paymentInstrument.setPaymentMethodTypeId(paymentMethods?.iterator().next().id)

        user.paymentInstruments.add(paymentInstrument)

        render template: '/payment/paymentMethods', model: [paymentMethods : paymentMethods , paymentInstruments : user.paymentInstruments , accountTypeId : accountType?.id]
    }

    def refreshPaymentInstrument (){
        int currentIndex = params.int("currentIndex")

        def user = new UserWS()
        UserHelper.bindPaymentInformations(user ,params.int("modelIndex"), params)

        def accountType = AccountTypeDTO.get(params.int("accountTypeId"))

        securityValidator.validateCompany(accountType?.company?.id, Validator.Type.VIEW)

        def paymentMethods = accountType?.paymentMethodTypes

        def isCreditCard = isPaymentMethodCreditCard(user.paymentInstruments, paymentMethods)

        render template: '/payment/paymentMethods', model: [paymentMethods : paymentMethods , paymentInstruments : user.paymentInstruments , accountTypeId : accountType?.id , isCreditCard : isCreditCard]
    }

    def removePaymentInstrument (){
        def currentIndex = params.int("currentIndex")

        def user = new UserWS()
        UserHelper.bindPaymentInformations(user ,params.int("modelIndex"), params)

        def accountType = AccountTypeDTO.get(params.int("accountTypeId"))

        securityValidator.validateCompany(accountType?.company?.id, Validator.Type.EDIT)

        def paymentMethods = accountType?.paymentMethodTypes

        PaymentInformationWS removed = user.paymentInstruments.remove(currentIndex)
        log.debug("user instrument is: " + user.paymentInstruments)

        render template: '/payment/paymentMethods', model: [paymentMethods : paymentMethods , paymentInstruments : user.paymentInstruments , accountTypeId : accountType?.id]
    }

    def isPaymentMethodCreditCard(paymentInstruments, paymentMethodTypes) {
        def isCreditCard = false

        if (paymentInstruments) {
            paymentInstruments.each { instrument ->
                def paymentMethodTemplateName = PaymentMethodTypeDTO.get(instrument.paymentMethodTypeId).paymentMethodTemplate.templateName
                if (paymentMethodTemplateName == CommonConstants.PAYMENT_CARD) {
                    isCreditCard = true
                }
            }
        } else {
            if (paymentMethodTypes) {
                isCreditCard = paymentMethodTypes.first()?.paymentMethodTemplate?.templateName == CommonConstants.PAYMENT_CARD
            }
        }

        return isCreditCard
    }

    @Secured(["hasAnyRole('PAYMENT_30', 'PAYMENT_31')"])
    def transfer () {
        def payment
        def user

        try {
            payment = params.id ? webServicesSession.getPayment(params.int('id')) : new PaymentWS()

            if (payment?.deleted==1) {
                paymentNotFoundErrorRedirect(params.id)
                return
            }

            if (params.id) {
                PaymentBL paymentBL = new PaymentBL(params.int("id"))
                if (paymentBL.ifRefunded()) {
                    flash.error = 'validation.error.update.refunded.payment'
                    flash.args = [params.id]
                    redirect controller: 'payment', action: 'list'
                    return
                }
            }

            user = webServicesSession.getUserWS(payment?.userId ?: params.int('userId'))

        } catch (SessionInternalError e) {
            log.error("Could not fetch WS object", e)
            paymentNotFoundErrorRedirect(params.id)
            return
        }

        [ payment: payment, user: user, currencies: retrieveCurrencies() ]
    }

    def bindPaymentTransfer(paymentTransfer, params) {
        bindData(paymentTransfer, params)
        paymentTransfer.fromUserId = Integer.valueOf(params?.payment?.userId)
        paymentTransfer.paymentId = Integer.valueOf(params?.payment?.id)
        paymentTransfer.createdBy = session['company_id']
        paymentTransfer.paymentTransferNotes = params?.paymentTransferNotes
    }

    @Secured(["hasAnyRole('PAYMENT_30', 'PAYMENT_31')"])
    def confirmTransfer () {

        PaymentTransferWS paymentTransfer = new PaymentTransferWS()
        bindPaymentTransfer(paymentTransfer, params)
        try {
            webServicesSession.transferPayment(paymentTransfer)

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e);
            def payment = params?.payment?.id? webServicesSession.getPayment(params?.payment?.id?.toInteger()) : new PaymentWS()
            def user = webServicesSession.getUserWS(payment?.userId ?: params.int('userId'))
            render view: 'transfer', model: [payment: payment, user: user, currencies: retrieveCurrencies()]
            return
        }

        flash.message = 'prompt.payment.transfer.successfully'
        flash.args = [paymentTransfer.toUserId]
        chain action: 'list', params: [ id: paymentTransfer.paymentId]

    }

}
