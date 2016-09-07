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

import com.sapienter.jbilling.client.ViewUtils
import com.sapienter.jbilling.client.util.Constants
import com.sapienter.jbilling.client.util.SortableCriteria
import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.server.filter.JbillingFilterConverter
import com.sapienter.jbilling.server.invoice.db.InvoiceDTO
import com.sapienter.jbilling.server.mediation.JbillingMediationErrorRecord
import com.sapienter.jbilling.server.mediation.JbillingMediationRecord
import com.sapienter.jbilling.server.mediation.MediationConfigurationWS
import com.sapienter.jbilling.server.mediation.MediationProcess
import com.sapienter.jbilling.server.order.db.OrderDTO
import com.sapienter.jbilling.server.security.Validator
import com.sapienter.jbilling.server.user.UserBL
import com.sapienter.jbilling.server.user.UserDTOEx
import com.sapienter.jbilling.server.user.db.CompanyDAS
import com.sapienter.jbilling.server.util.PreferenceBL
import com.sapienter.jbilling.server.process.ProcessStatusWS
import com.sapienter.jbilling.server.util.IWebServicesSessionBean
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.util.SecurityValidator
import com.sapienter.jbilling.server.util.csv.CsvExporter
import com.sapienter.jbilling.client.util.DownloadHelper
import com.sapienter.jbilling.server.util.csv.Exporter

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.criterion.CriteriaSpecification
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import sun.reflect.generics.reflectiveObjects.NotImplementedException

/**
 * MediationController
 *
 * @author Vikas Bodani
 * @since 17/02/2011
 */
@Secured(["MENU_95"])
class MediationController {

    static pagination = [max: 10, offset: 0, sort: 'id', order: 'desc']

    // Matches the columns in the JQView grid with the corresponding field
    static final viewColumnsToFields =
            ['processId': 'id',
             'startDate': 'startDate',
             'endDate': 'endDate',
             'orders': 'recordsProcessed']

    IWebServicesSessionBean webServicesSession
    ViewUtils viewUtils

    def recentItemService
    def breadcrumbService
    def filterService
    def mediationService
    def mediationProcessService
    def companyService
    SecurityValidator securityValidator


    def index (){
        list()
    }

    def list (){
        def filters  = filterService.getFilters(FilterType.MEDIATIONPROCESS, params)
        breadcrumbService.addBreadcrumb(controllerName, actionName, null, null)


        def isMediationProcessRunning = webServicesSession.isMediationProcessRunning();
        if (isMediationProcessRunning) {
            flash.info = 'mediation.config.prompt.running'
        }

        def configurations = webServicesSession.getAllMediationConfigurations() as List
        def hasNonGlobalConfig = false
        for (MediationConfigurationWS config : configurations) {
            hasNonGlobalConfig |= !config.global
        }

        def usingJQGrid = PreferenceBL.getPreferenceValueAsBoolean(session['company_id'], Constants.PREFERENCE_USE_JQGRID);
        //If JQGrid is showing, the data will be retrieved when the template renders
        if (usingJQGrid){
            if (params.applyFilter || params.partial) {
                render template: 'processesTemplate', model: [filters: filters,
                                                              isMediationProcessRunning: isMediationProcessRunning,
                                                              hasNonGlobalConfig: hasNonGlobalConfig]
            } else {
                render view: "list", model: [filters: filters,
                                             isMediationProcessRunning: isMediationProcessRunning,
                                             hasNonGlobalConfig: hasNonGlobalConfig]
            }

            return
        }

        List<com.sapienter.jbilling.server.filter.Filter> convertedFilters = JbillingFilterConverter.convert(filters);
        convertedFilters.add(new com.sapienter.jbilling.server.filter.Filter("entityId", com.sapienter.jbilling.server.filter.FilterConstraint.EQ, "" + session['company_id']));
        params.max = params.max ? Integer.parseInt(params.max): 10
        params.sort = params.sort ? params.sort : "startDate"
        params.order = params.order ? params.order : "desc"

        List<MediationProcess> processes = mediationProcessService.findMediationProcessByFilters(session['company_id'], params.page ?: 0, params.max, params.sort, params.order, convertedFilters)

        if (params.applyFilter || params.partial) {
            render template: 'processesTemplate', model: [processes: processes, filters: filters,
                                                                        isMediationProcessRunning: isMediationProcessRunning,
                                                                        hasNonGlobalConfig: hasNonGlobalConfig]
        } else {
            render view: "list", model: [processes: processes, filters: filters,
                                         isMediationProcessRunning: isMediationProcessRunning,
                                         hasNonGlobalConfig: hasNonGlobalConfig]
        }
    }

    def findProcesses (){
        def filters = filterService.getFilters(FilterType.MEDIATIONPROCESS, params)

        params.sort = viewColumnsToFields[params.sidx]
        params.order  = params.sord
        params.max = params.rows
        params.offset = params?.page ? (params.int('page')-1) * params.int('rows') : 0
        params.alias = SortableCriteria.NO_ALIAS

        List<MediationProcess> processes = getFilteredProcesses(filters, params)
        try {
            render getProcessesJsonData(processes, params) as JSON

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e)
            render e.getMessage()
        }

    }

    /**
     * Converts Mediation processes to JSon
     */
    private def Object getProcessesJsonData(processes, GrailsParameterMap params) {
        def jsonCells = processes
        def currentPage = params.page ? params.int('page') : 1
        def rowsNumber = params.rows ? params.int('rows'): 1
        def numberOfPages = Math.ceil(jsonCells.size / rowsNumber.intValue())

        def jsonData = [rows: jsonCells, page: currentPage, records: jsonCells.size, total: numberOfPages]

        jsonData
    }

    def getFilteredProcesses (filters, params) {
		params.max = (params?.max?.toInteger()) ?: pagination.max
        params.page = (params?.page?.toInteger()) ? ((params.page > 0) ? (params.page - 1): params.page): 0
        params.sort = params.sort ? params.sort : "startDate"
        params.order = params.order ? params.order : "desc"

        List<com.sapienter.jbilling.server.filter.Filter> convertedFilters = JbillingFilterConverter.convert(filters);
        convertedFilters.add(new com.sapienter.jbilling.server.filter.Filter("entityId", com.sapienter.jbilling.server.filter.FilterConstraint.EQ, "" + session['company_id']));

        List<MediationProcess> processes = mediationProcessService.findMediationProcessByFilters(session['company_id'], 0, params.max, params.sort, params.order, convertedFilters)

        return processes
    }

    def show (){
        MediationProcess process = mediationProcessService.getMediationProcess(uuid(params.get('id')))
        if (process == null) {
            redirect action: 'list'
        } else {
            securityValidator.validateCompany(process?.entityId, Validator.Type.VIEW)
            def ordersCreatedCount = mediationService.getOrdersForMediationProcess(process?.id).size()

            def latestProcessStatus = webServicesSession.getMediationProcessStatus()
            def canBeUndone = null != latestProcessStatus &&
                    latestProcessStatus.getMediationProcessId().compareTo(process.id) == 0 &&
                    !latestProcessStatus.getState().equals(ProcessStatusWS.State.RUNNING) &&
                    process.entityId == session['company_id']

            recentItemService.addRecentItem(process.id, RecentItemType.MEDIATIONPROCESS)
            breadcrumbService.addBreadcrumb(controllerName, actionName, null, process.id)

            if (params.template) {
                render template: params.template, model: [
                        selected: process, canBeUndone: canBeUndone,
                        invoicesCreatedCount: 0,//TODO MODULARIZATION, THIS WAS RETRIEVED BY THE MEDIATION SYSTEM
                        ordersCreatedCount: ordersCreatedCount]
            } else {
                def filters = filterService.getFilters(FilterType.MEDIATIONPROCESS, params)
                def processes = mediationProcessService.findLatestMediationProcess(session['company_id'], 0, 100) //TODO MODULARIZATION HERE THE FILTERING WAS NOT WORKING IN THE RIGHT WAY

                render view: 'list', model: [
                        selected: process, canBeUndone: canBeUndone,
                        processes: processes, filters: filters,
                        invoicesCreatedCount: 0,//TODO MODULARIZATION, THIS WAS RETRIEVED BY THE MEDIATION SYSTEM
                        ordersCreatedCount: ordersCreatedCount]
            }
        }
    }

    def showMediationRecords (){

        def processId = uuid(params.get('id'))
        def entityId = params.int('entityId') ?: session['company_id']
        securityValidator.validateCompany(params.int('entityId'), Validator.Type.VIEW)
        log.debug "Submitting for entityId {$entityId}"
        def currency = CompanyDTO.get(session['company_id']).currency
		int CDR_PAGE_SIZE = 25
        if (params.first == 'true') {
            params.offset= null
        }

		DateTimeFormatter dtf = DateTimeFormat.forPattern(message(code: 'date.format'))
		def startDate= params.event_start_date ? dtf.parseDateTime(params.event_start_date).toDate() : null
		def endDate= params.event_end_date ? dtf.parseDateTime(params.event_end_date).toDate() : null

        def filters = Arrays.asList(
                new com.sapienter.jbilling.server.filter.Filter("jBillingCompanyId", com.sapienter.jbilling.server.filter.FilterConstraint.EQ, entityId),
                new com.sapienter.jbilling.server.filter.Filter("processId", com.sapienter.jbilling.server.filter.FilterConstraint.EQ, processId),
                new com.sapienter.jbilling.server.filter.Filter("eventDate", com.sapienter.jbilling.server.filter.FilterConstraint.DATE_BETWEEN, startDate, endDate)
        );
        def records = mediationService.findMediationRecordsByFilters(params.offset?:0, CDR_PAGE_SIZE, filters)

        def record
        if (records) {
            record = records?.get(0)
        }
        render view: 'events', model: [records: records, record: record, currency: currency, selectionEntityId: entityId, processId: processId]
	}

    def showMediationErrors () {
        def processId = uuid(params.get('id'))
        def entityId = params?.selectedEntity ? params.int('selectedEntity'):session['company_id']
        securityValidator.validateCompany(params.int('selectedEntity'), Validator.Type.VIEW)
        int CDR_PAGE_SIZE = 25
        if (params.first == 'true') {
            params.offset = null
        }

        def filters = Arrays.asList(
                new com.sapienter.jbilling.server.filter.Filter("jBillingCompanyId", com.sapienter.jbilling.server.filter.FilterConstraint.EQ, entityId),
                new com.sapienter.jbilling.server.filter.Filter("processId", com.sapienter.jbilling.server.filter.FilterConstraint.EQ, processId),
        );
        def mediationErrorRecords = mediationService.findMediationErrorRecordsByFilters(params.offset?:0, CDR_PAGE_SIZE, filters)
        def record
        if (mediationErrorRecords) {
            record = mediationErrorRecords?.get(0)
        }
        List companies = []
        def currentCompany = CompanyDTO.get(session['company_id'] as Integer)
        if (new CompanyDAS().isRoot(session['company_id'] as Integer)) {
            companies = CompanyDTO.findAllByParent(currentCompany)
            companies?.sort({ a, b -> a.description <=> b.description } as Comparator)
            companies.add(0, currentCompany)
        }
        render view: 'errors', model: [records: mediationErrorRecords, record: record, offset:params.offset?:0, companies:companies, selected:entityId ]
    }

    def mediationRecordsCsv (){
        def processId = uuid(params.get('id'))
        def records = mediationService.getMediationRecordsForProcess(processId);

        params.max = CsvExporter.MAX_RESULTS

        if (records.size() > CsvExporter.MAX_RESULTS) {
            flash.error = message(code: 'error.export.exceeds.maximum')
            flash.args = [CsvExporter.MAX_RESULTS]
                redirect action: 'list', id: params.id

        } else {
            DownloadHelper.setResponseHeader(response, "mediation_records.csv")
            Exporter<JbillingMediationRecord> exporter = CsvExporter.createExporter(JbillingMediationRecord.class);
            render text: exporter.export(records), contentType: "text/csv"
        }
    }

    def mediationErrorsCsv (){
        def processId = uuid(params.get('id'))
        def mediationErrorRecords = mediationService.getMediationErrorRecordsForProcess(processId);

        if (mediationErrorRecords.size() > CsvExporter.MAX_RESULTS) {
            flash.error = message(code: 'error.export.exceeds.maximum')
            flash.args = [CsvExporter.MAX_RESULTS]
                redirect action: 'list', id: params.id

        } else {
            DownloadHelper.setResponseHeader(response, "mediation_errors.csv")
            Exporter<JbillingMediationErrorRecord> exporter = CsvExporter.createExporter(JbillingMediationErrorRecord.class);
            render text: exporter.export(mediationErrorRecords), contentType: "text/csv"
        }
    }

    def invoice (){

        def invoiceId = uuid(params.get('id'))
        params.status=null
        def invoice = InvoiceDTO.get(invoiceId)
        securityValidator.validateUserAndCompany(UserBL.getWS(new UserDTOEx(invoice?.baseUser)), Validator.Type.VIEW)
        def records = mediationSession.getMediationRecordLinesForInvoice(invoiceId)
        log.debug ("Events found ${records.size}")
        def processId= null;
		def record
        if (records) {
            session[CDR_NEXT_PAGE_OFFSET]= getHBaseKey(order, records.last())
            record = records?.get(0)
            processId = record.getProcessId()
        } else {
            flash.info = message(code: 'event.mediation.records.not.available')
            flash.args = [params.id, params.status]
        }
        render view: 'events', model: [invoice: invoice, records: records, record: record, processId: processId]
    }

    def order (){

        def orderId = uuid(params.get('id'))
        params.status= null
        def order, records, record
		int CDR_PAGE_SIZE = 25
		def CDR_NEXT_PAGE_OFFSET= 'CDR_NEXT_PAGE_OFFSET'

		def startDate= params.event_start_date ? new Date().parse(message(code: 'date.format'), params.event_start_date) : null
		def endDate= params.event_end_date ? new Date().parse(message(code: 'date.format'), params.event_end_date) : null

		/*println "startDate: ${startDate}"
		println "endDate: ${endDate}"
		println "offset: ${session[CDR_NEXT_PAGE_OFFSET]}"*/

        def processId= null;
        try {
            order = OrderDTO.get(orderId)
            securityValidator.validateUserAndCompany(UserBL.getWS(new UserDTOEx(order?.baseUserByUserId)), Validator.Type.VIEW)

			if (params.first == 'true') {
					session[CDR_NEXT_PAGE_OFFSET]= null
			}

            records = mediationService.getMediationRecordsForOrder(orderId)
            log.debug ("Events found ${records.size}")

			if (records) {
            	record = records?.get(0)
                processId = record.getProcessId()
			} else {
                flash.info = message(code: 'event.mediation.records.not.available')
                flash.args = [params.id, params.status]
            }
        } catch (Exception e) {
            flash.info = message(code: 'error.mediation.events.none')
            flash.args = [params.id]
        }
        render view: 'events', model: [order: order, records: records, event_start_date: startDate, event_end_date: endDate, CDR_PAGE_SIZE: CDR_PAGE_SIZE,
                                       record: record, orderEvents: true, processId: processId]
    }

    def orderRecordsCsv = {
        //TODO MODULARIZATION: FIX THIS
        throw new NotImplementedException();
//        def orderId = params.int('id')
//        def records = mediationSession.getMediationRecordLinesForOrder(orderId)
//
//        params.max = CsvExporter.MAX_RESULTS
//
//        if (records.size() > CsvExporter.MAX_RESULTS) {
//            flash.error = message(code: 'error.export.exceeds.maximum')
//            flash.args = [CsvExporter.MAX_RESULTS]
//            redirect action: 'list', id: params.id
//        } else {
//            DownloadHelper.setResponseHeader(response, "mediation_records.csv")
//            Exporter<MediationRecordLineDTO> exporter = CsvExporter.createExporter(MediationRecordLineDTO.class);
//            render text: exporter.export(records), contentType: "text/csv"
//        }
    }

    def csv (){
        def filters = filterService.getFilters(FilterType.MEDIATIONPROCESS, params)

        params.sort = viewColumnsToFields[params.sidx] != null ? viewColumnsToFields[params.sidx] : params.sort
        params.order = params.sord
        params.max = CsvExporter.MAX_RESULTS

        List<MediationProcess> processes = getFilteredProcesses(filters, params)

        if (processes.size() > CsvExporter.MAX_RESULTS) {
            flash.error = message(code: 'error.export.exceeds.maximum')
            flash.args = [CsvExporter.MAX_RESULTS]
            redirect action: 'list', id: params.id

        } else {
            DownloadHelper.setResponseHeader(response, "mediationProcesses.csv")
            Exporter<MediationProcess> exporter = CsvExporter.createExporter(MediationProcess.class);
            render text: exporter.export(processes), contentType: "text/csv"
        }
    }

    def undo (){
        def processId = uuid(params.get('id'))

        try {
            webServicesSession.undoMediation(processId)
        } catch (Exception e) {
            log.debug("mediation process can not be undone")
            flash.clear()
            viewUtils.resolveException(flash, session.locale, e)
        }
        list()
    }

	/**
	 * COPIED from MediationRecordLineDTO
	 * @param entityId
	 * @param orderId
	 * @param orderLineId
	 * @param userId
	 * @param key
	 * @return
	 */
	private getHBaseKey(OrderDTO order, JbillingMediationRecord record) {
		StringBuilder keyBuilder = new StringBuilder();
		keyBuilder.append(order?.baseUserByUserId?.company?.id).append("-");
		if (order?.baseUserByUserId?.id) {
			keyBuilder.append("usr-").append(order.baseUserByUserId.id).append("-");

			if (null != order && null != record) {
				keyBuilder.append("ord-").append(order.id).append("-");
				keyBuilder.append("orl-").append(record.orderLineId).append("-");
			}
		}
		return keyBuilder.toString();
	}

    def recycleProcessCDRs() {
        try {
            UUID processId = uuid(params.get('id'))
            log.debug "Triggering recycle mediation for processId ID ${processId}"
            webServicesSession.runRecycleForMediationProcess (processId)
        } catch (SessionInternalError e){
            viewUtils.resolveException(flash, session.locale, e);
        } catch (Exception e) {
            log.error e.getMessage()
            flash.error = 'mediation.config.recycle.failure'
            return
        }

        redirect action: 'list'
    }

    def refreshMediationCounter(){
        try {
            Integer processId = params.id as Integer
            MediationProcess process = mediationProcessService.getMediationProcess(uuid(params.get('id')))
            securityValidator.validateCompany(process?.entityId, Validator.Type.VIEW)
            mediationProcessService.updateMediationProcessCounters(processId)
        } catch (SessionInternalError e){
            viewUtils.resolveException(flash, session.locale, e);
        } catch (Exception e) {
            log.error e.getMessage()
            flash.error = 'mediation.config.refresh.counter.failure'
            return
        }
        redirect action: 'show', params:params
    }

    def uuid(String uuidString) {
        return uuidString == null ? null: UUID.fromString(uuidString)
    }
}
