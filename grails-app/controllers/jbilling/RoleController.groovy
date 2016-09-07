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

import com.sapienter.jbilling.client.util.SortableCriteria
import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.csrf.RequiresValidFormToken
import com.sapienter.jbilling.server.user.db.UserDTO
import com.sapienter.jbilling.server.security.Validator
import com.sapienter.jbilling.server.user.permisson.db.RoleDTO
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.user.permisson.db.PermissionTypeDTO
import com.sapienter.jbilling.server.user.RoleBL
import com.sapienter.jbilling.server.user.permisson.db.PermissionDTO
import com.sapienter.jbilling.server.util.Constants
import com.sapienter.jbilling.server.util.PreferenceBL
import com.sapienter.jbilling.server.util.SecurityValidator
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

/**
 * RoleController 
 *
 * @author Brian Cowdery
 * @since 02/06/11
 */
@Secured(["MENU_99"])
class RoleController {

    static pagination = [max: 10, offset: 0, sort: 'id', order: 'desc']

    static final viewColumnsToFields =
            ['roleId': 'id']
    
	static scope = "prototype"
    def springSecurityService
    def userService
    def breadcrumbService
    def viewUtils
    SecurityValidator securityValidator


    def index () {
        flash.invalidToken = flash.invalidToken
        redirect action: 'list', params: params
    }

    def getList(params) {
		
		def company_id = session['company_id'] as Integer 
		
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset
		params.sort = params?.sort ?: pagination.sort
		params.order = params?.order ?: pagination.order
        def languageId = session['language_id']

        RoleDTO loggedInUserRole = UserDTO.get(springSecurityService.principal.id).roles?.first()
        return RoleDTO.createCriteria().list(
                max:    params.max,
                offset: params.offset
        ) {
			eq('company', new CompanyDTO(company_id))
            if ( params.roleId ) {
                def searchParam = params.roleId
                if (searchParam.isInteger()){
                    eq('id', Integer.valueOf(searchParam));
                } else {
                    searchParam = searchParam.toLowerCase()
                    sqlRestriction(
                            """ exists (
                                            select a.foreign_id
                                            from international_description a
                                            where a.foreign_id = {alias}.id
                                            and a.table_id =
                                             (select b.id from jbilling_table b where b.name = ? )
                                            and a.language_id = ?
                                            and a.psudo_column = 'title'
                                            and lower(a.content) like ?
                                        )
                                    """,[Constants.TABLE_ROLE,languageId,searchParam]
                    )
                }
            }
            if (loggedInUserRole.roleTypeId != Constants.TYPE_SYSTEM_ADMIN) {
                ne('roleTypeId', Constants.TYPE_SYSTEM_ADMIN)
            }
            SortableCriteria.sort(params, delegate)
		}
    }

    def list () {
        
        def selected = params.id ? RoleDTO.get(params.int('id')) : null
        securityValidator.validateCompany(selected?.company?.id, Validator.Type.VIEW)
		// if id is present and object not found, give an error message to the user along with the list
        if (params.id?.isInteger() && selected == null) {
			flash.error = 'role.not.found'
            flash.args = [params.id]
        }

		breadcrumbService.addBreadcrumb(controllerName, 'list',
			selected?.getTitle(session['language_id']), selected?.id, selected?.getDescription(session['language_id']))
		
        def usingJQGrid = PreferenceBL.getPreferenceValueAsBoolean(session['company_id'], Constants.PREFERENCE_USE_JQGRID);
        //If JQGrid is showing, the data will be retrieved when the template renders
        if (usingJQGrid){
            if (params.applyFilter || params.partial) {
                render template: 'rolesTemplate', model: [selected: selected ]
            }else {
                render view: 'list', model: [selected: selected ]
            }
            return
        }

        def roles = getList(params)
        if (params.applyFilter || params.partial) {
            render template: 'rolesTemplate', model: [ roles: roles, selected: selected ]
        } else {
            render view: 'list', model: [ roles: roles, selected: selected ]
        }
    }

    def findRoles () {
        params.sort = viewColumnsToFields[params.sidx]
        params.order = params.sord
        params.max = params.rows
        params.offset = params?.page ? (params.int('page') - 1) * params.int('rows') : 0
        params.alias = SortableCriteria.NO_ALIAS

        def roles = getList(params)

        try {
            def jsonData = getRolesJsonData(roles, params)

            render jsonData as JSON

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e)
            render e.getMessage()
        }
    }

    /**
     * Converts Roles to JSon
     */
    private def Object getRolesJsonData(roles, GrailsParameterMap params) {
        def jsonCells = roles
        def currentPage = params.page ? Integer.valueOf(params.page) : 1
        def rowsNumber = params.rows ? Integer.valueOf(params.rows): 1
        def totalRecords =  jsonCells ? jsonCells.totalCount : 0
        def numberOfPages = Math.ceil(totalRecords / rowsNumber)

        def jsonData = [rows: jsonCells, page: currentPage, records: totalRecords, total: numberOfPages]

        jsonData
    }

    def show () {
        def role = RoleDTO.get(params.int('id'))
        securityValidator.validateCompany(role?.company?.id, Validator.Type.VIEW)

        breadcrumbService.addBreadcrumb(controllerName, 'list', role.getTitle(session['language_id']), role.id, role.getDescription(session['language_id']))

        render template: 'show', model: [ selected: role ]
    }

    @Secured(["CONFIGURATION_1903"])
    def edit () {
        def role = chainModel?.role
        if (!role) {
            if (params.id) {
                role = RoleDTO.get(params.int('id'))
                securityValidator.validateCompany(role?.company?.id, Validator.Type.VIEW)
            }
            else {
                role = new RoleDTO()
            }
        }

        if (role == null) {
        	redirect action: 'list', params:params
            return
        }
        
        def permissionTypes = PermissionTypeDTO.list(order: 'asc')

        def crumbName = params.id ? role?.getTitle(session['language_id']) : null
        def crumbDescription = params.id ? role?.getDescription(session['language_id']) : null
        breadcrumbService.addBreadcrumb(controllerName, actionName, crumbName, params.int('id'), crumbDescription)

		def roleTitle = chainModel?.roleTitle;
		def roleDescription = chainModel?.roleDescription;
		def validationError = chainModel?.validationError ? true : false;

        [ role: role, permissionTypes: permissionTypes, roleTitle:roleTitle, roleDescription:roleDescription, validationError:validationError ]
    }

    @Secured(["CONFIGURATION_1903"])
    @RequiresValidFormToken
    def save () {
    	
    	def role = new RoleDTO();
    	role.company = CompanyDTO.get(session['company_id'])
	    bindData(role, params, 'role')
    	def roleTitle = params.role.title == null ?: params.role.title.trim();
    	def roleDescription = params.role.description == null ?: params.role.description.trim();
    	def languageId = session['language_id'];
    	
    	try {
	
            List<PermissionDTO> allPermissions = PermissionDTO.list()
            params.permission.each { id, granted ->
                if (granted) {
                    role.permissions.add(allPermissions.find { it.id == id as Integer })
                }
            }

			def isNonEmptyRoleTitle = params.role.title ? !params.role.title.trim().isEmpty() : false;
			if (isNonEmptyRoleTitle) {
	            def roleService = new RoleBL();
	
	            // save or update
	            if (!role.id || role.id == 0) {
	                log.debug("saving new role ${role}")
	                roleService.validateDuplicateRoleName(roleTitle, languageId, role.company.id)
	                role.id = roleService.create(role)
					roleService.updateRoleType(role.id)
	
	                flash.message = 'role.created'
	                flash.args = [role.id as String]
	
	            } else {
	                log.debug("updating role ${role.id}")
	
	                roleService.set(role.id)
	                
	                if (!roleService.getEntity()?.getDescription(languageId, Constants.PSUDO_COLUMN_TITLE)?.equalsIgnoreCase(roleTitle)) {
	                	roleService.validateDuplicateRoleName(roleTitle, languageId, role.company.id)
	                }
	                
	                roleService.update(role)
	
	                flash.message = 'role.updated'
	                flash.args = [role.id as String]
	            }
	
	            // set/update international descriptions
                roleService.setInternationalDescriptions(roleTitle, roleDescription, languageId);
	            chain action: 'list', params: [id: role.id]
	        } else {
				
	            String [] errors = ["RoleDTO,title,role.error.title.empty"]
				throw new SessionInternalError("Description is missing ", errors);            
	        }
        
        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e)
            chain action: 'edit', model: [ role:role, roleTitle:roleTitle, roleDescription:roleDescription, validationError:true ]
        }
    }

    @Secured(["CONFIGURATION_1903"])
    def delete () {
        try {
            if (params.id) {
                securityValidator.validateCompany(RoleDTO.get(params.int('id'))?.company?.id, Validator.Type.VIEW)
                userService.deleteRole(params.int('id'), session['language_id']);

                log.debug("Deleted role ${params.id}.")
            }

            flash.message = 'role.deleted'
            flash.args = [params.id]

            // render the partial role list
            params.applyFilter = true
            params.id = null
            redirect action: 'list'
        } catch (SessionInternalError e) {
            flash.error = 'Can not delete role '+params.id+', it is in use.'
            redirect action: 'list'
        }
    }
}
