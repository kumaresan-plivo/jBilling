package jbilling

import com.sapienter.jbilling.client.util.DownloadHelper
import com.sapienter.jbilling.server.customer.CustomerBL
import com.sapienter.jbilling.server.ediTransaction.EDIStatisticWS
import com.sapienter.jbilling.server.ediTransaction.db.EDIFileDAS
import com.sapienter.jbilling.server.ediTransaction.db.EDIFileDTO
import com.sapienter.jbilling.server.ediTransaction.db.EDITypeDTO
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.user.db.UserDAS
import com.sapienter.jbilling.server.user.db.UserDTO
import com.sapienter.jbilling.server.util.IWebServicesSessionBean
import grails.plugin.springsecurity.annotation.Secured
import org.joda.time.DateTime

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

@Secured(["MENU_903"])
class EdiReportController {

    static scope = "prototype"
    static pagination = [max: 10, offset: 0, sort: 'id', order: 'desc']
    static versions = [ max: 25 ]
    def messageSource

    IWebServicesSessionBean webServicesSession
    def viewUtils
    def filterService
    def recentItemService
    def breadcrumbService

    public static DateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy")

    def index () {
        list()
    }

    def list () {
        render view: 'list'
    }

    def ediStatistics () {
        if (!params.startDate) {
            params.startDate = simpleDateFormat.format(new DateTime().dayOfMonth().withMinimumValue().toDate())
        }

        if (!params.endDate) {
            params.endDate = simpleDateFormat.format(new DateTime().dayOfMonth().withMaximumValue().toDate())
        }

        Date startDate, endDate;
        try {
            startDate = simpleDateFormat.parse(params.startDate)
            endDate = simpleDateFormat.parse(params.endDate)
        }
        catch (ParseException e) {
            viewUtils.resolveException(flash, session.locale, e);
        }

        Integer ediTypeId = params.ediTypeId ? params.ediTypeId as Integer : null

        List<EDIStatisticWS> statistics = new EDIFileDAS().getEdiStatistics(session['company_id'] as Integer, startDate, endDate, ediTypeId)

        render view: 'ediStatistics', model: [statistics: statistics, ediTypes: getEdiTypes(), ediTypeId: ediTypeId]
    }

    def ediStatisticsWithExceptions () {
        if (!params.startDate) {
            params.startDate = simpleDateFormat.format(new DateTime().dayOfMonth().withMinimumValue().toDate())
        }

        if (!params.endDate) {
            params.endDate = simpleDateFormat.format(new DateTime().dayOfMonth().withMaximumValue().toDate())
        }

        Date startDate, endDate;
        try {
            startDate = simpleDateFormat.parse(params.startDate)
            endDate = simpleDateFormat.parse(params.endDate)
        }
        catch (ParseException e) {
            viewUtils.resolveException(flash, session.locale, e);
        }

        Integer ediTypeId = params.ediTypeId ? params.ediTypeId as Integer : null

        List<EDIStatisticWS> statistics = new EDIFileDAS().getEdiStatisticsWithExceptions(session['company_id'] as Integer, startDate, endDate, ediTypeId)

        render view: 'ediStatisticsWithExceptions', model: [statistics: statistics, ediTypes: getEdiTypes(), ediTypeId: ediTypeId]
    }

    def currentEdiExceptions () {
        EDIFileDAS ediFileDAS = new EDIFileDAS()
        Integer companyId = session['company_id'] as Integer
        Integer ediTypeId = params.ediTypeId ? params.ediTypeId as Integer : null

        List<EDIFileDTO> ediFiles = ediFileDAS.getEDIFilesWithExceptions(companyId, ediTypeId, params.int('max') ?: 10, params.int('offset') ?: 0)
        Integer ediFilesTotalCount = ediFileDAS.getEDIFilesWithExceptionsCount(companyId, ediTypeId)

        render view: 'currentEdiExceptions', model: [ediFiles: ediFiles, ediFilesTotalCount: ediFilesTotalCount, ediTypes: getEdiTypes(), ediTypeId: ediTypeId]
    }

    def customersNotInvoiced () {
        UserDAS userDAS = new UserDAS();
        Integer companyId = session['company_id'] as Integer

        DateTime firstDayOfMonth = new DateTime().dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
        DateTime startOfNextMont = firstDayOfMonth.plusMonths( 1 ).dayOfMonth().withMinimumValue().withTimeAtStartOfDay();

        Date startDate, endDate

        if(!params.startDate){
            params.startDate = simpleDateFormat.format(firstDayOfMonth.toDate());
        }

        if(!params.endDate){
            params.endDate = simpleDateFormat.format(startOfNextMont.toDate());
        }

        try {
            startDate = simpleDateFormat.parse(params.startDate)
        }
        catch (ParseException e) {
            flash.error = 'edi.report.from.date.invalid'
            startDate = firstDayOfMonth.toDate()
        }

        try {
            endDate = simpleDateFormat.parse(params.endDate)
        }
        catch (ParseException e) {
            flash.error = 'edi.report.to.date.invalid'
            endDate = startOfNextMont.toDate()
        }

        int max = params.int('max') ?: 10
        int offset = params.int('offset') ?: 0

        List<UserDTO> users = userDAS.getUsersNotInvoiced(companyId,startDate, endDate, max, offset)
        Integer usersTotals = userDAS.getUsersNotInvoicedCount(companyId,startDate, endDate)

        render view: 'customersNotInvoiced', model: [users: users, usersTotal: usersTotals, startDate: startDate, endDate: endDate]
    }

    def downloadRegulatoryComplianceReport() {
        Integer companyId = session['company_id'] as Integer
        CustomerBL bl = new CustomerBL()
        File report = bl.createRegulatoryComplianceReport(companyId)
        DownloadHelper.setResponseHeader(response, "compliance_report.txt")

        render file: report , contentType: "text/txt"
    }

    private List<EDITypeDTO> getEdiTypes() {
        return EDITypeDTO.findAllByEntity(CompanyDTO.get(session['company_id'] as Integer), [sort: "name"])
    }
}