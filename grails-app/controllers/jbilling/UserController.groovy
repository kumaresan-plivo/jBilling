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
import com.sapienter.jbilling.client.user.UserHelper
import com.sapienter.jbilling.client.util.FlowHelper
import com.sapienter.jbilling.client.util.SortableCriteria
import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.license.exception.LicenseExpiredException
import com.sapienter.jbilling.license.exception.LicenseInvalidException
import com.sapienter.jbilling.license.exception.LicenseMissingException
import com.sapienter.jbilling.csrf.RequiresValidFormToken
import com.sapienter.jbilling.server.security.Validator
import com.sapienter.jbilling.server.user.UserBL
import com.sapienter.jbilling.server.user.UserCodeWS
import com.sapienter.jbilling.server.user.UserDTOEx
import com.sapienter.jbilling.server.user.UserWS
import com.sapienter.jbilling.server.user.contact.db.ContactDTO
import com.sapienter.jbilling.server.user.db.CompanyDTO
import com.sapienter.jbilling.server.user.db.UserCodeDTO
import com.sapienter.jbilling.server.user.db.UserCodeLinkDAS
import com.sapienter.jbilling.server.user.db.UserDTO
import com.sapienter.jbilling.server.user.permisson.db.PermissionDTO
import com.sapienter.jbilling.server.user.permisson.db.PermissionTypeDTO
import com.sapienter.jbilling.server.user.permisson.db.RoleDTO
import com.sapienter.jbilling.server.util.Constants
import com.sapienter.jbilling.server.util.IWebServicesSessionBean
import com.sapienter.jbilling.server.util.PreferenceBL
import com.sapienter.jbilling.server.util.SecurityValidator
import com.sapienter.jbilling.server.util.db.EnumerationDTO

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.plugin.springsecurity.SpringSecurityUtils

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.apache.commons.lang.ArrayUtils

import org.hibernate.FetchMode as FM
import org.hibernate.criterion.CriteriaSpecification
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Restrictions
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter

@Secured(["MENU_99"])
class UserController {
	static scope = "prototype"
    static pagination = [max: 10, offset: 0, sort: 'id', order: 'desc']

    static final viewColumnsToFields =
            ['userId': 'id',
             'userName': 'contact.lastName, contact.firstName',
             'organization': 'contact.organizationName']

    IWebServicesSessionBean webServicesSession
    ViewUtils viewUtils

    def breadcrumbService
    def recentItemService
    def springSecurityService
    def securitySession
    def userService
    SecurityValidator securityValidator


    def index () {
        flash.invalidToken = flash.invalidToken
        redirect action: 'list', params: params
    }

    def getList(params) {
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset
        params.sort = params?.sort ?: pagination.sort
        params.order = params?.order ?: pagination.order

        def company_id = session['company_id']
        RoleDTO loggedInUserRole = UserDTO.get(springSecurityService.principal.id).roles?.first()
        return UserDTO.createCriteria().list(
                max: params.max,
                offset: params.offset
        ) {
            and {
                or {
                    isEmpty('roles')
                    roles {
                        ne('roleTypeId', Constants.TYPE_CUSTOMER)
                        ne('roleTypeId', Constants.TYPE_PARTNER)
                        if (loggedInUserRole.roleTypeId != Constants.TYPE_SYSTEM_ADMIN) {
                            ne('roleTypeId', Constants.TYPE_SYSTEM_ADMIN)
                        }
                    }
                }

                eq('company', new CompanyDTO(company_id))
                eq('deleted', 0)
                createAlias('contact', 'contact', CriteriaSpecification.LEFT_JOIN)
                if(params.userId) {
                    eq('id', params.int('userId'))
                }
                if(params.organization) {
                    addToCriteria(Restrictions.ilike("contact.organizationName",  params.organization, MatchMode.ANYWHERE) );
                }
                if(params.userName) {
                    or{
                        addToCriteria(Restrictions.ilike("userName",  params.userName, MatchMode.ANYWHERE))
                        addToCriteria(Restrictions.ilike("contact.firstName", params.userName, MatchMode.ANYWHERE))
                        addToCriteria(Restrictions.ilike("contact.lastName", params.userName, MatchMode.ANYWHERE))
                    }
                }
            }
            SortableCriteria.sort(params, delegate)
        }
    }

    def list () {
        def currentUser = springSecurityService.principal
        def user
        def selected = params.id ? UserDTO.get(params.int("id")) : null

        if(selected) {
            securityValidator.validateUserAndCompany(UserBL.getWS(new UserDTOEx(selected)), Validator.Type.VIEW)
        }

        def contact = selected ? ContactDTO.findByUserId(selected.id) : null
        if (selected?.roles?.first()?.roleTypeId == Constants.TYPE_SYSTEM_ADMIN)
            if (UserDTO.get(currentUser.id)?.roles?.first()?.roleTypeId != Constants.TYPE_SYSTEM_ADMIN)
                redirect controller: 'list', action: 'list'
		
        def crumbDescription = selected ? UserHelper.getDisplayName(selected, contact) : null
        breadcrumbService.addBreadcrumb(controllerName, 'list', null, selected?.id, crumbDescription)
        user = selected ? webServicesSession.getUserWS(params.int('id')) : new UserWS()
		if (selected?.deleted == 1) {
            flash.message = 'user.edit.deleted'
            flash.args = [ params.id ]
        }

        def usingJQGrid = PreferenceBL.getPreferenceValueAsBoolean(session['company_id'], Constants.PREFERENCE_USE_JQGRID);
        //If JQGrid is showing, the data will be retrieved when the template renders
        if (usingJQGrid){
            if (params.applyFilter || params.partial) {
                render template: 'usersTemplate', model: [selected: selected, currentUser: currentUser, contact: contact]
            }else {
                [selected: selected, currentUser: currentUser, contact: contact]
            }
            return
        }

        def users = getList(params)
        //Check if account is locked so that it can be shown on UI appropriately
        if( selected && user?.id) {
            selected.setAccountLocked(user?.isAccountLocked)
        }

        if (params.applyFilter || params.partial) {
            render template: 'usersTemplate', model: [users: users, selected: selected, currentUser: currentUser, contact: contact]
        } else {
            [users: users, selected: selected, currentUser: currentUser, contact: contact]
        }
    }

    /**
     * JQGrid will call this method to get the list as JSon data
     */
    def findUsers () {
        params.sort = viewColumnsToFields[params.sidx]
        params.order = params.sord
        params.max = params.rows
        params.offset = params?.page ? (params.int('page') - 1) * params.int('rows') : 0
        params.alias = SortableCriteria.NO_ALIAS

        def users = getList(params)

        try {
            def jsonData = getUsersJsonData(users, params)

            render jsonData as JSON

        } catch (SessionInternalError e) {
            viewUtils.resolveException(flash, session.locale, e)
            render e.getMessage()
        }
    }

    /**
     * Converts Users to JSon
     */
    private def Object getUsersJsonData(users, GrailsParameterMap params) {
        def jsonCells = users
        def currentPage = params.page ? Integer.valueOf(params.page) : 1
        def rowsNumber = params.rows ? Integer.valueOf(params.rows): 1
        def totalRecords =  jsonCells ? jsonCells.totalCount : 0
        def numberOfPages = Math.ceil(totalRecords / rowsNumber)

        def jsonData = [rows: jsonCells, page: currentPage, records: totalRecords, total: numberOfPages]

        jsonData
    }

    /* Source of alternate flows
       - MyAccountController
     */
    @Secured(["isFullyAuthenticated()"])
    def show () {
        def currentUser = springSecurityService.principal
        def user

        //load the user by id or user code
        if(params['id']) {
            user = UserDTO.get(params.int('id'))
            securityValidator.validateUserAndCompany(UserBL.getWS(new UserDTOEx(user)), Validator.Type.VIEW)
        } else {
            user = new UserBL().findUserCodeForIdentifier(params['userCode'], session['company_id']).user
            securityValidator.validateUserAndCompany(UserBL.getWS(new UserDTOEx(user)), Validator.Type.VIEW)
        }
        def contact = user ? ContactDTO.findByUserId(user.id) : null
        user.accountLocked = new UserBL(user.id).isAccountLocked()

        breadcrumbService.addBreadcrumb(controllerName, 'list', null, user.userId, UserHelper.getDisplayName(user, contact))

        if (flash.altView || params.partial){
            FlowHelper.display(this, true, [ template: '/user/show',
                                             model: [ selected: user, currentUser: currentUser, contact: contact ]])
        }else{
            redirect(controller: "myAccount", action: "index")
        }
    }

    /*
     Source of alternate flows
      - MyAccountController
     */
	@Secured(["hasAnyRole('MY_ACCOUNT_161', 'MY_ACCOUNT_162','ROLE_SUPER_USER','ROLE_SYSTEM_ADMIN')"])
    def edit () {
        def user
        def contacts

        try {
            user = params.id ? webServicesSession.getUserWS(params.int('id')) : new UserWS()
            if (user.deleted==1)
            {
                redirect controller: 'user', action: 'list'
                return
            }
            contacts = params.id ? (webServicesSession.getUserContactsWS(user.userId) as List) : null

        } catch (SessionInternalError e) {
            log.error("Could not fetch WS object", e)

            flash.error = 'user.not.found'
            flash.args = [params.id as String]

            redirect controller: 'user', action: 'list'
            return
        }
		
		def company_id = session['company_id']
        def company = CompanyDTO.createCriteria().get {
            eq("id", company_id)
            fetchMode('contactFieldTypes', FM.JOIN)
        }
        UserDTO loggedInUser = UserDTO.get(springSecurityService.principal.id)
		FlowHelper.display(this, true, [ view: '/user/edit',
			model: [ user: user, contacts: contacts, company: company, roles: loadRoles(), loggedInUser: loggedInUser ]])

    }



    /* Source of alternate flows
        - /myAccount/editUser
     */
    /**
     * Validate and save a user.
     */
	@Secured(["hasAnyRole('MY_ACCOUNT_161', 'MY_ACCOUNT_162','ROLE_SUPER_USER','ROLE_SYSTEM_ADMIN')"])
    def save () {
        def user = new UserWS()
        UserHelper.bindUser(user, params)
        def contacts = []

		def userId= params['user']['userId'] as Integer

		log.debug "Save called for user ${userId}"

		def oldUser = userId ? webServicesSession.getUserWS(userId) : null

		def company_id = session['company_id']
        def company = CompanyDTO.createCriteria().get {
            eq("id", company_id)
            fetchMode('contactFieldTypes', FM.JOIN)
        }

		//edit my account fields permission
		if ( !oldUser || SpringSecurityUtils.ifAllGranted('ROLE_SUPER_USER') || SpringSecurityUtils.ifAllGranted('ROLE_SYSTEM_ADMIN') || SpringSecurityUtils.ifAllGranted('MY_ACCOUNT_162') ) {
			UserHelper.bindContacts(user, contacts, company, params)
		} else {
			user= oldUser
			contacts= userId ? webServicesSession.getUserContactsWS(userId) : null
		}
        

		//change password permission
		if ( !oldUser || SpringSecurityUtils.ifAllGranted('ROLE_SUPER_USER') || SpringSecurityUtils.ifAllGranted('ROLE_SYSTEM_ADMIN') || SpringSecurityUtils.ifAllGranted('MY_ACCOUNT_162') ) {
			UserHelper.bindPassword(user, oldUser, params, flash)
		} else {
			user.password= null
		}
		UserDTO loggedInUser = UserDTO.get(springSecurityService.principal.id)
        if (flash.error) {
			UserHelper.bindContacts(user, contacts, company, params)
            FlowHelper.display(this, false, [ view: '/user/edit',
                    model: [ user: user, contacts: contacts, company: company, loggedInUser: loggedInUser, roles: loadRoles() ]])

            return
        }

        try {
            // save or update
            if (!oldUser) {
                log.debug("creating user ${user}")

                user.userId = webServicesSession.createUser(user)

                flash.message = 'user.created'
                flash.args = [user.userId as String]

            } else {
                log.debug("saving changes to user ${user.userId}")

                webServicesSession.updateUser(user)

                flash.message = 'user.updated'
                flash.args = [user.userId as String]
            }
        } catch (SessionInternalError e) {
            flash.clear()
            viewUtils.resolveException(flash, session.locale, e)
			contacts = userId ? webServicesSession.getUserContactsWS(userId) : null
            if(!contacts && !userId){
                contacts = [user.getContact()]
            }
            FlowHelper.display(this, false, [ view: '/user/edit',
                    model: [ user: user, contacts: contacts, company: company, loggedInUser: loggedInUser, roles: loadRoles()]])

            return
        }

		if ( SpringSecurityUtils.ifAnyGranted("MENU_99") || SpringSecurityUtils.ifAnyGranted("ROLE_SUPER_USER") || SpringSecurityUtils.ifAnyGranted("ROLE_SYSTEM_ADMIN") ) {
            FlowHelper.display(this, true, [
                    model: [ user: user, contacts: contacts, company: company]],
                    { chain action: 'list', params: [ id: user.userId ] } )
		} else {
            FlowHelper.display(this, true, [
                    model: [ user: user, contacts: contacts, company: company]],
                    { chain action: 'edit', params: [ id: user.userId ] } )
		}
    }

    def delete () {
        try {
            if (params.id) {
                webServicesSession.deleteUser(params.int('id'))
                log.debug("Deleted user ${params.id}.")
            }

            flash.message = 'user.deleted'
            flash.args = [params.id as String]

            // render the partial user list
            params.applyFilter = true
        } catch (SessionInternalError e) {
            flash.error = message(code: "user.validation.cannot.delete.itself")
            redirect(controller: 'user', action: 'list', params: params)
            return
        }
        redirect(action: 'list')
    }

    def permissions () {
        def user
        try {
            user = params.id ? webServicesSession.getUserWS(params.int('id')) : new UserWS()

        } catch (SessionInternalError e) {
            log.error("Could not fetch WS object", e)

            flash.error = 'user.not.found'
            flash.args = [ params.id as String ]

            redirect controller: 'user', action: 'list'
            return
        }

        def contact = user ? ContactDTO.findByUserId(user.userId) : null
        breadcrumbService.addBreadcrumb(controllerName, 'permissions', null, user.userId, UserHelper.getDisplayName(user, contact))

        // combined user and role permissions
        def permissions = new UserBL(user.userId).getPermissions()

        // user's main role
        def role = RoleDTO.createCriteria().get() {
            eq('company', new CompanyDTO(session['company_id']))
            eq('roleTypeId', user.mainRoleId)
        }

        // permission types
        def permissionTypes = PermissionTypeDTO.list(order: 'asc')

        [ user: user, contact: contact, permissions: permissions, role: role, permissionTypes: permissionTypes ]
    }

    @RequiresValidFormToken
    def savePermissions () {
        Set<PermissionDTO> userPermissions = new HashSet<PermissionDTO>()
        List<PermissionDTO> allPermissions = PermissionDTO.list()
        params.permission.each { id, granted ->
            if (granted) {
                userPermissions.add(allPermissions.find{ it.id == id as Integer })
            }
        }

        // save
        userService.savePermissions(userPermissions, params.int('id'))

        flash.message = 'permissions.updated'
        flash.args = [ params.id ]

        chain action: 'list', params: [ id: params.id ]
    }

    @Secured('isAuthenticated()')
    def reload () {
        log.debug("reloading session attributes for user ${springSecurityService.principal.username}")

        securitySession.setAttributes(request, response, springSecurityService.principal)
        reloadURL(null)

    }

    def reloadURL(String errorMessage) {
        breadcrumbService.load()
        recentItemService.load()

        def breadcrumb = breadcrumbService.getLastBreadcrumb()
        if (breadcrumb) {
            // show last page viewed
            redirect(controller: breadcrumb.controller, action: breadcrumb.action, id: breadcrumb.objectId)
            if(errorMessage) {
                flash.errorAuthToFail = errorMessage
            }
        } else {
            // show default page
            redirect(controller: 'customer')
        }
    }

    @Secured('isAuthenticated()')
	def getAdminByCompany () {
        CompanyDTO companyDTO = CompanyDTO.get(params.entityId)
        securityValidator.validateCompany(companyDTO?.id, Validator.Type.VIEW)
		def users = UserDTO.findAllByCompany(companyDTO, [max: 1, sort: "id", order: "asc"])
		if(users.size() > 0) {
			render(contentType: "text/json") {name = users?.get(0).userName + ";" + params.entityId}
		}
	}
	
	@Secured('isAuthenticated()')
	def getUserByCompany (){
		def user = UserDTO.get(session["user_id"]);
		if(user) {
			render(contentType: "text/json") {name = user?.userName + ";" + params.entityId}
		}
	}

    private List loadRoles() {
        RoleDTO loggedInUserRole = UserDTO.get(springSecurityService.principal.id).roles?.first()
        return RoleDTO.createCriteria().list() {
            eq('company', new CompanyDTO(session['company_id']))
			ne('roleTypeId', Constants.TYPE_CUSTOMER)
			ne('roleTypeId', Constants.TYPE_PARTNER)
			if(loggedInUserRole?.roleTypeId != Constants.TYPE_SYSTEM_ADMIN)
				ne('roleTypeId', Constants.TYPE_SYSTEM_ADMIN)
            order('id', 'asc')
        }
    }

    /**
     * Display the list of user codes
     */
    @Secured(["hasAnyRole('MENU_99, USER_140, USER_141,USER_142')"])
    def userCodeList () {
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset
        params.sort = params?.sort ?: pagination.sort
        params.order = params?.order ?: pagination.order


        UserDTO user = UserDTO.get(params.int('id'))
        securityValidator.validateUserAndCompany(UserBL.getWS(new UserDTOEx(user)), Validator.Type.VIEW)

        //add breadcrumb
        breadcrumbService.addBreadcrumb(controllerName, 'userCodeList', null, params.int('id'), user?.userName)

        if(params['filterBy'] == null) params.active = 'on'

        params.put("userId", params.id)
        params.put("showActive", 'on' == params.active ? 1 : 0)

        def userCodes
        try {
            userCodes = userService.getFilteredUserCodes(params)
        } catch (SessionInternalError e) {
            userCodes = []
            viewUtils.resolveException(flash, session.locale, e);
        }

        def model = [userCodes: userCodes, user: user] + isLoggedInUserPartnerModel()

        if (params.applyFilter || params.partial) {
            render(template: 'userCodeList', model: model)
        } else {
            render(view: 'userCodeListView', model: model)
        }
    }

    @Secured(["hasAnyRole('MENU_99, USER_140, USER_141,USER_142')"])
    def userCodeShow () {
        if (!params.id) {
            flash.error = 'userCode.not.selected'
            flash.args = [ params.id  as String]

            redirect controller: 'user', action: 'userCodeList'
            return
        }

        //load the user code
        def userCode = UserCodeDTO.get(params.id)
        securityValidator.validateUserAndCompany(new UserBL(userCode?.user?.id).getUserWS(), Validator.Type.VIEW)

        if (params.id) {
            breadcrumbService.addBreadcrumb(controllerName, actionName, 'show', params.int('id'), userCode.identifier)
        }

        //if we must show a template
        if(params.template) {
            render template: 'userCodeShow', model: [ userCode : userCode, user: userCode.user ]

            //else show the user code list
        } else {

            def userCodes = UserCodeDTO.createCriteria().list(
                    max:    params.max
            ) { eq("user.id", userCode.user.id)
            }

            render view: 'userCodeListView', model: [userCodes: userCodes, selectedUserCode: userCode, user: userCode.user]
        }
    }

    /**
     * Deactivate the selected user code. Set the expiry date to today.
     */
    @Secured(["hasAnyRole('MENU_99, USER_140, USER_141,USER_142')"])
    def userCodeDeactivate () {
        def userCode = params.id ? UserCodeDTO.get(params.int('id')) : null
        securityValidator.validateUserAndCompany(new UserBL(userCode?.user?.id).getUserWS(), Validator.Type.EDIT)

        if (!userCode) {
            flash.error = 'userCode.not.found'
            flash.args = [ params.id  as String]

            redirect controller: 'user', action: 'userCodeList'
            return
        }

        UserCodeWS userCodeWs = UserBL.convertUserCodeToWS(userCode)
        userCodeWs.validTo = new Date()

        try {
            //if the user has access update the user code
            if (SpringSecurityUtils.ifAllGranted("USER_141")) {
                webServicesSession.updateUserCode(userCodeWs)
            } else {
                render view: '/login/denied'
                return;
            }
        } catch (SessionInternalError e) {
            //got an exception, show the edit page again
            viewUtils.resolveException(flash, session.locale, e);
        }

        redirect( action: 'userCodeList', id: params.userId )
    }

    @Secured(["hasAnyRole('MENU_99, USER_140, USER_141,USER_142')"])
    def userCodeEdit () {
        def userCode = params.id ? UserCodeDTO.get(params.int('id')) : new UserCodeDTO()
        securityValidator.validateUserAndCompany(new UserBL(userCode?.user?.id ?: params.userId.toInteger()).getUserWS(), Validator.Type.VIEW)

        if (params.id && !userCode) {
            flash.error = 'userCode.not.found'
            flash.args = [ params.id  as String]

            redirect controller: 'user', action: 'userCodeList'
            return
        }

        //if id is not provided we must be adding
        if (!params.id && !params.boolean('add')) {
            flash.error = 'userCode.not.selected'
            flash.args = [ params.id  as String]

            redirect controller: 'user', action: 'userCodeList'
            return
        }

        //if we are adding we must know which user it belongs to
        if (params.boolean('add') && !params.userId) {
            flash.error = 'userCode.user.not.selected'
            flash.args = [ params.userId  as String]

            redirect controller: 'user', action: 'userCodeList'
            return
        }

        if (params.boolean('add')) {
            UserDTO user = UserDTO.get(params.userId)
            userCode.user= user
            userCode.validFrom = new Date()
        }

        EnumerationDTO typesEnum = EnumerationDTO.createCriteria().get() {
            eq('name', 'User Code Type')
            eq('entityId', session['company_id'])
        }



        //if we are editing we can create a breadcrumb
        if (params.id) {
            breadcrumbService.addBreadcrumb(controllerName, actionName, 'update', params.int('id'), userCode.identifier)
        }

        boolean isEditable = true;
        if(userCode.id) {
            isEditable = new UserCodeLinkDAS().countLinkedObjects(userCode.id) == 0
        }
        [ userCode : userCode, types: typesEnum?.values?.collect {it.value} , user: userCode.user, isEditable: isEditable ]
    }

    @Secured(["hasAnyRole('MENU_99, USER_140, USER_141,USER_142')"])
    @RequiresValidFormToken
    def userCodeSave () {
        def userCode = new UserCodeWS()

        //bind the parameters to the user code
        bindData(userCode, params, [exclude: ['id', 'identifier']])

        userCode.id = !params.id?.equals('') ? params.int('id') : 0
        userCode.identifier = params.userName + params.identifier.trim()
        userCode.userId = params.int('userId')

        try {
            if (userCode.id) {
                //if the user has access update the user code
                if (SpringSecurityUtils.ifAllGranted("USER_141")) {
                    webServicesSession.updateUserCode(userCode)
                } else {
                    render view: '/login/denied'
                    return;
                }
            } else {
                //if the user has permission add the user code
                if (SpringSecurityUtils.ifAllGranted("USER_140")) {
                    webServicesSession.createUserCode(userCode)
                } else {
                    render view: '/login/denied'
                    return;
                }
            }
        } catch (SessionInternalError e) {
            //got an exception, show the edit page again
            viewUtils.resolveException(flash, session.locale, e);
            def dto = new UserBL().converUserCodeToDTO(userCode);
            dto.discard()
            dto.validTo = null

            EnumerationDTO typesEnum = EnumerationDTO.createCriteria().get() {
                eq('name', 'User Code Type')
                eq('entityId', session['company_id'])
            }

            boolean isEditable = true;
            if(userCode.id) {
                isEditable = new UserCodeLinkDAS().countLinkedObjects(userCode.id) == 0
            }
            render view: 'userCodeEdit', model: [ userCode : dto, types: typesEnum?.values?.collect {it.value}, user: UserDTO.get(userCode.userId), isEditable: isEditable] + isLoggedInUserPartnerModel()
            return
        }

        redirect action: 'userCodeList', id: userCode.userId
    }

    def isLoggedInUserPartnerModel() {
        // #7043 - Agents && Commissions - If a Partner is logged in we have to change the layout to panels to avoid showing the configuration menus.
        UserDTO loggedInUser = UserDTO.get(springSecurityService.principal.id)

        def model = [isPartner: loggedInUser.partner ? true : false]
        model
    }
	
	private List<CompanyDTO> getAccessibleCompanies(UserWS user) {
		List<CompanyDTO> accessibleCompanies= new ArrayList<CompanyDTO>();
		if( !ArrayUtils.isEmpty(user.accessibleEntityIds) ) {
			accessibleCompanies = CompanyDTO.createCriteria().list(){
				'in'('id', user.accessibleEntityIds)
			}
		}
		accessibleCompanies
	}

    def failToSwitch() {

        String msg = ''
        def exception = session[AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY]
        if (exception) {
            if (exception instanceof AccountExpiredException) {
                msg = g.message(code: "springSecurity.errors.login.expired")
            }
            else if (exception instanceof CredentialsExpiredException) {
                msg = g.message(code: "springSecurity.errors.login.passwordExpired")
            }
            else if (exception instanceof DisabledException) {
                msg = g.message(code: "springSecurity.errors.login.disabled")
            }
            else if (exception instanceof LockedException) {
                msg = g.message(code: "springSecurity.errors.login.locked")
            }
            else if (exception instanceof LicenseMissingException) {
                msg = 'auth.fail.license.missing.exception'
            }
            else if (exception instanceof LicenseInvalidException) {
                msg = 'auth.fail.license.invalid.exception'
            }
            else if (exception instanceof LicenseExpiredException) {
                msg = 'auth.fail.license.expired.exception'
            }
        }
        reloadURL(msg)
    }
}
