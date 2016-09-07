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
import com.sapienter.jbilling.server.audit.Audit
import com.sapienter.jbilling.server.item.db.PlanDTO
import com.sapienter.jbilling.server.security.Validator
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.util.IWebServicesSessionBean;
import com.sapienter.jbilling.server.util.PreferenceBL
import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.server.item.TariffPlan
import com.sapienter.jbilling.client.util.SortableCriteria
import com.sapienter.jbilling.server.util.SecurityValidator
import com.sapienter.jbilling.server.util.csv.CsvExporter
import com.sapienter.jbilling.client.util.DownloadHelper
import com.sapienter.jbilling.server.util.csv.Exporter
import com.sapienter.jbilling.server.item.ItemBL
import com.sapienter.jbilling.server.item.db.ItemDTO
import com.sapienter.jbilling.server.util.db.InternationalDescriptionDTO
import com.sapienter.jbilling.server.util.db.LanguageDTO
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

import org.hibernate.criterion.CriteriaSpecification
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Restrictions


/**
 * PlanController
 *
 * @author Brian Cowdery
 * @since 01-Feb-2011
 */
@Secured(["MENU_98"])
class PlanController {
	static scope = "prototype"
    static pagination = [ max: 10, offset: 0, sort: 'id', order: 'desc' ]
    static versions = [ max: 25 ]

    // Matches the columns in the JQView grid with the corresponding field
    static final viewColumnsToFields =
            ['planId': 'id',
             'company': 'ce.description',
             'itemNumber': 'i.internalNumber']

    IWebServicesSessionBean webServicesSession
    def viewUtils
    def filterService
    def breadcrumbService
	def companyService
    def auditBL
    SecurityValidator securityValidator


    def index () {
        list()
    }

	def getFilteredPlans(filters, params) {

        def languageId = session['language_id']
        def plans = PlanDTO.createCriteria().list(
                max:    params.max,
                offset: params.offset
        ) {
			and {
				filters.each { filter ->
					if (filter.value) {
						if(filter.field == 'tariff') {
							boolean tariff = filter.stringValue?.equals(TariffPlan.TARIFF.name()) ? true : false
							addToCriteria(Restrictions.eq("tariff", tariff))
						} else{
                            addToCriteria(filter.getRestrictions());
                        }
                    }
				}
				
				createAlias "item", "i"
				createAlias "i.entities", "ce", CriteriaSpecification.LEFT_JOIN
				createAlias "i.entity", "planEntity", CriteriaSpecification.LEFT_JOIN
				or {
						'in'('ce.id', companyService.getEntityAndChildEntities()*.id) // showing plans accessible to this company
						and {
							eq('planEntity.id', companyService.getRootCompanyId())
							eq('i.global', true)
						}
					}
                if (params.planId){
                    def searchParam = params.planId
                    if (searchParam.isInteger()){
                        eq('id', Integer.valueOf(searchParam));
                    } else {
                        searchParam = searchParam.toLowerCase()
                        sqlRestriction(
                                """ exists (
                                            select a.foreign_id
                                            from international_description a
                                            where a.foreign_id = {alias}.item_id
                                            and a.table_id =
                                             (select b.id from jbilling_table b where b.name = ? )
                                            and a.language_id = ?
                                            and a.psudo_column = 'description'
                                            and lower(a.content) like ?
                                        )
                                    """, [com.sapienter.jbilling.server.util.Constants.TABLE_ITEM, languageId, "%" + searchParam + "%"]
                        )
                    }
                }
                if (params.company){
                    addToCriteria(Restrictions.ilike("ce.description",  params.company, MatchMode.ANYWHERE) );
                }
                if (params.itemNumber){
                    addToCriteria(Restrictions.ilike("i.internalNumber",  params.itemNumber, MatchMode.ANYWHERE) );
                }
			}
            // apply sorting
            SortableCriteria.buildSortNoAlias(params, delegate)
        }
		
		return plans.unique()
    }
    
    /**
     * Get a list of plans and render the list page. If the "applyFilters" parameter is given, the
     * partial "_plans.gsp" template will be rendered instead of the complete list.
     */
    def list () {
		def filters = filterService.getFilters(FilterType.PLAN, params)
		
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset
        params.sort = params?.sort ?: pagination.sort
        params.order = params?.order ?: pagination.order

        def selected = params.id ? PlanDTO.get(params.int("id")) : null
		
		if (selected) {
            if (!companyService.isAvailable(selected?.item?.global, selected?.item?.entity?.id, companyService.getEntityAndChildEntities()*.id)) {
				selected= null
			}
		}
		
        // if id is present and plan not found, give an error message along with the list
        if (params.id?.isInteger() && selected == null) {
            flash.error = 'plan.not.found'
            flash.args = [params.id]
        }

        breadcrumbService.addBreadcrumb(controllerName, 'list', null, selected ? params.int('id') : null, selected?.item?.internalNumber)

        def canBeDeleted = selected ? new ItemBL().canBeDeleted(selected.item.id) : false

        def usingJQGrid = PreferenceBL.getPreferenceValueAsBoolean(session['company_id'], Constants.PREFERENCE_USE_JQGRID);
        //If JQGrid is showing, the data will be retrieved when the template renders
        if (usingJQGrid){
            if (params.applyFilter || params.partial) {
                render template: 'plansTemplate', model: [selected: selected]
            }else {
                render view: 'list', model: [selected: selected, filters : filters, canBeDeleted: canBeDeleted ]
            }
            return
        }

        def plans = getFilteredPlans(filters, params)
        if (params.applyFilter || params.partial) {
            render template: 'plansTemplate', model: [ plans: plans, selected: selected ]
        } else {
            render view: 'list', model: [ plans: plans, selected: selected, filters : filters, canBeDeleted: canBeDeleted ]
        }
    }

    def findPlans () {
        def filters = filterService.getFilters(FilterType.PLAN, params)

        params.sort = viewColumnsToFields[params.sidx]
        params.order  = params.sord
        params.max = params.rows
        params.offset = params?.page ? (params.int('page')-1) * params.int('rows') : 0
        params.alias = SortableCriteria.NO_ALIAS

        def plans = getFilteredPlans(filters, params)

        try {
            render getAsJsonData(plans, params) as JSON

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e)
            render e.getMessage()
        }

    }

    /**
     * Converts * to JSon
     */
    private def Object getAsJsonData(elements, GrailsParameterMap params) {
        def jsonCells = elements
        def currentPage = params.page ? Integer.valueOf(params.page) : 1
        def rowsNumber = params.rows ? Integer.valueOf(params.rows): 1
        def totalRecords =  jsonCells ? jsonCells.totalCount : 0
        def numberOfPages = Math.ceil(totalRecords / rowsNumber)

        def jsonData = [rows: jsonCells, page: currentPage, records: totalRecords, total: numberOfPages]

        jsonData
    }

    /**
     * Shows details of the selected plan.
     */
    @Secured(["PLAN_63"])
    def show () {
        PlanDTO plan = PlanDTO.get(params.int('id'))
        securityValidator.validateCompany(session['company_id'], companyService.getEntityAndChildEntities()*.id, plan?.item?.global, Validator.Type.VIEW)
        breadcrumbService.addBreadcrumb(controllerName, 'list', null, params.int('id'), plan.item.internalNumber)
        def canBeDeleted = new ItemBL().canBeDeleted(plan.item.id);
        render template: 'show', model: [ plan: plan, canBeDeleted: canBeDeleted ]
    }

    /**
     * Deletes the given plan id and all the plan item prices.
     */
    @Secured(["PLAN_62"])
    def delete () {
        if (params.id) {
            try {
                def plan = webServicesSession.getPlanWS(params.int('id'))
                def canBeDeleted = new ItemBL().canBeDeleted(plan.itemId);
                if (canBeDeleted) {
                    webServicesSession.deletePlan(plan.id)
                    webServicesSession.deleteItem(plan.itemId)

                    log.debug("Deleted plan ${params.id} and subscription product ${plan.itemId}.")

                    flash.message = 'plan.deleted'
                    flash.args = [ params.id ]
                    params.id = null
                } else {
                    log.error("Could not delete plan ${params.id}.")
                    def item = ItemDTO.get(plan.itemId)
                    flash.error = 'plan.cannot.be.deleted'
                    flash.args = [ item?.internalNumber ]
                }
            } catch (SessionInternalError e) {
                log.error("Could not fetch WS object", e)

                flash.error = 'plan.not.found'
                flash.args = [ params.id ]
                params.id = null
            }
        }

        // render the partial plan list
        params.applyFilter = true
        params.id = null
        redirect action: 'list'
		
    }

    /**
     * Applies the set filters to the product list, and exports it as a CSV for download.
     */
    @Secured(["PLAN_63"])
    def csv () {
        def filters = filterService.getFilters(FilterType.INVOICE, params)

        params.sort = viewColumnsToFields[params.sidx]
        params.order = params.sord
        params.offset = params?.page ? (params.int('page') - 1) * params.int('rows') : 0
        params.alias = SortableCriteria.NO_ALIAS
        params.max = CsvExporter.MAX_RESULTS

        def plans = getFilteredPlans(filters, params)

        if (plans.totalCount > CsvExporter.MAX_RESULTS) {
            flash.error = message(code: 'error.export.exceeds.maximum')
            flash.args = [ CsvExporter.MAX_RESULTS ]
            redirect action: 'list', id: params.id

        } else {
            DownloadHelper.setResponseHeader(response, "plans.csv")
            Exporter<PlanDTO> exporter = CsvExporter.createExporter(PlanDTO.class);
            render text: exporter.export(plans), contentType: "text/csv"
        }
    }

    @Secured(["PLAN_63"])
    def history (){
        def plan = PlanDTO.get(params.int('id'))
        def records = []
        if (plan) {
            securityValidator.validateCompany(plan?.item?.entity?.id, Validator.Type.VIEW)
            def currentMultiLangDescription = new TreeMap<String, String>()
            def currentMultiLangVersions = new ArrayList<Audit>()
            LanguageDTO.list().each { language ->
                InternationalDescriptionDTO description = plan.getItem().getDescriptionDTO(language.getId())
				if(description) {
	                currentMultiLangDescription.put(language.getDescription(), description.getContent());
	                currentMultiLangVersions.add(getAuditVersionsForDescription(description, language))
				}
            }
            records = [
                    [ name: 'description', id: plan.id, current: currentMultiLangDescription, versions: currentMultiLangVersions.flatten() ]
            ]
        }

        render view: '/audit/history', model: [ records: records, historyid: plan.id ]
    }

    private List<Audit> getAuditVersionsForDescription(InternationalDescriptionDTO description, language) {
        def auditVersionsForLanguage = auditBL.get(InternationalDescriptionDTO.class,
                description.getAuditKey(description.getId()), versions.max)
        auditVersionsForLanguage.each { a ->
            TreeMap oldColumns = a.getColumns()
            def newColumns = new TreeMap<String, String>()
            newColumns.put(language.getDescription(), oldColumns.get("content"))
            a.setColumns(newColumns)
        }
        return auditVersionsForLanguage
    }

    def restore (){
        switch (params.record) {
            case "description":
                def plan = PlanDTO.get(params.int('historyid'))
                securityValidator.validateCompany(plan?.item?.entity?.id, Validator.Type.EDIT)
                LanguageDTO.list().each {lang ->
                    def description = plan.getItem().getDescriptionDTO(lang.id)
					if(description) {
                    List<Audit> audits = auditBL.get(InternationalDescriptionDTO.class,
                            description.getAuditKey(description.getId()), versions.max)
                    audits.each {audit ->
                        if (audit.getTimestamp() == Long.parseLong(params.get('timestamp'))) {
                            auditBL.restore(description, description.id, Long.parseLong(params.get('timestamp')))
                        }}
					}
                }
                break;
        }

        redirect controller: 'planBuilder', action: 'edit', params: [ id: params.historyid ]
    }
	
	def retrieveChildCompanies() {
		return CompanyDTO.findAllByParent(CompanyDTO.get(session['company_id']))
	}

	def retrieveCompanies() {
		def childs = retrieveChildCompanies()
		childs.add(CompanyDTO.get(session['company_id']))
		return childs;
	}
}
