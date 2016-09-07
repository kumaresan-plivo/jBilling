package jbilling

import com.sapienter.jbilling.common.SessionInternalError
import com.sapienter.jbilling.server.util.Constants
import com.sapienter.jbilling.server.item.db.AssetDAS
import com.sapienter.jbilling.server.order.db.OrderDAS
import com.sapienter.jbilling.server.order.db.OrderLineDAS
import com.sapienter.jbilling.server.payment.db.PaymentDAS
import com.sapienter.jbilling.server.provisioning.ProvisioningCommandType
import com.sapienter.jbilling.server.provisioning.ProvisioningCommandWS
import com.sapienter.jbilling.server.provisioning.ProvisioningRequestStatus
import com.sapienter.jbilling.server.provisioning.db.ProvisioningCommandDTO
import grails.plugin.springsecurity.annotation.Secured

@Secured(["MENU_900"])
class ProvisioningController {

    static pagination = [max: 10, offset: 0, sort: 'id', order: 'asc']

    def webServicesSession
    def viewUtils
    def filterService
    def recentItemService
    def breadcrumbService

    def index () {
        list()
    }

    def showCommands () {

        store_params()

        session.provisioningShow = "CMD"
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset
        params.sort = params?.sort ?: pagination.sort
        params.order = params?.order ?: pagination.order

        def filters = filterService.getFilters(FilterType.PROVISIONING_CMD, params)

        def commands = null;
        try {
            commands = getFilteredCommands(filters)
        } catch (Exception e){
            return response.sendError(Constants.ERROR_CODE_404)
        }
        def selected = null
        def size = null
        if (commands.size() > 0) {
            size = commands.size()
            breadcrumbService.addBreadcrumb(controllerName, 'showCommands', null, null)
            if (commands != null ) {
				if (params.id != null) {
					selected = commands.find {it.id == params.int('id')}
					if(params.show){
						//called by clicking any particular command.
						render template: "showCommand", model:[commands: commands, filters: filters, selected: selected, typeId:  selected?.getOwningEntityId()]
						return
					}
				}
                commands = commands.subList(params.offset, params.offset + (params.max>=commands.size()?commands.size():params.max))
            }
        }

        if (params.applyFilter || params.partial) {
            render template: "listCommands", model: [commands: commands, filters: filters, selected: selected, typeId:  selected?.getOwningEntityId(), totalCount: size]
        } else {
            render view: "showProvisioningCommand", model: [commands: commands, filters: filters, selected: selected, typeId: selected?.getOwningEntityId(), totalCount: size]
        }
    }

    def callCommandsList (){

        store_params()

        session.provisioningShow = "CMD"
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset
        params.sort = params?.sort ?: pagination.sort
        params.order = params?.order ?: pagination.order

        def filters = filterService.getFilters(FilterType.PROVISIONING_CMD, params)
		breadcrumbService.addBreadcrumb(controllerName, 'showCommands', null, null)
        def commands = null;
        commands = getFilteredCommands()
        def selected = null
        def size = null
        if (commands.size() > 0) {
            selected = commands.get(0)
            size = commands.size()
            commands = commands.subList(params.offset, (params.offset + (params.max>=commands.size()?commands.size():params.max)<commands.size())?(params.offset + (params.max>=commands.size()?commands.size():params.max)):commands.size())
        }

        render template: "listCommands", model: [commands: commands, filters: filters, typeId:  selected?.getOwningEntityId(), totalCount: size]
    }

    def callRequestsList (){

        store_params()

        session.provisioningShow = "REQ"
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset
        params.sort = params?.sort ?: pagination.sort
        params.order = params?.order ?: pagination.order

        def filters = filterService.getFilters(FilterType.PROVISIONING_REQ, params)

        def commands = null;
        if (getParamType()) {
            commands = webServicesSession.getProvisioningCommands(getParamType(), (session.provisioningTypeId as int)) as List
        } else {
            def companyId = session['company_id'].toInteger()
            commands = companyId ? webServicesSession.getCommandsByEntityId(companyId) : null
        }

        if (commands == null || commands.size() <= 0) {
            redirect(action: "callCommandsList")
            return
        }
        ProvisioningCommandWS command = webServicesSession.getProvisioningCommandById(session.provisioningSelectedId as int)

        def provisioningRequests = webServicesSession.getProvisioningRequests((session.provisioningSelectedId as int))

        def size = provisioningRequests.size()
        provisioningRequests = provisioningRequests.subList(0, params.offset, (params.offset + (params.max>=provisioningRequests.size()?provisioningRequests.size():params.max)<provisioningRequests.size())?(params.offset + (params.max>=provisioningRequests.size()?provisioningRequests.size():params.max)):provisioningRequests.size())

        def selected = null

        if (provisioningRequests.size() > 0) {
            selected = provisioningRequests.get(0)
            if (params.id != null) {
                selected = provisioningRequests.find {it.id == params.int('id')}
                render template: "showRequest", model:[filters: filters, selected: selected]
                return
            }
        }

        render template: "listRequests", model: [requests: provisioningRequests, filters: filters, selected: selected, typeId:  selected?.getOwningEntityId(), totalCount: size]
    }

    def showRequests (){

        store_params()

        session.provisioningShow = "REQ"
        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset
        params.sort = params?.sort ?: pagination.sort
        params.order = params?.order ?: pagination.order

        def filters = filterService.getFilters(FilterType.PROVISIONING_REQ, params)

        def commands = null;
        if (getParamType()) {
            commands = webServicesSession.getProvisioningCommands(getParamType(), (session.provisioningTypeId as int)) as List
        } else {
            def companyId = session['company_id'].toInteger()
            commands = companyId ? webServicesSession.getCommandsByEntityId(companyId) : null
        }

        if (commands == null || commands.size() <= 0) {
            redirect(action: "showCommands")
            return
        }
        ProvisioningCommandWS command = webServicesSession.getProvisioningCommandById(session.provisioningSelectedId as int)

        breadcrumbService.addBreadcrumb(controllerName, 'showRequests', null, null, command.getName())

        def provisioningRequests = null
        provisioningRequests = getFilteredRequests(filters) as List
        def size = provisioningRequests.size()
        if (size != null){
            provisioningRequests = provisioningRequests.subList(params.offset, params.offset + (params.max>=provisioningRequests.size()?provisioningRequests.size():params.max))
        }
        def selected = null

        if (provisioningRequests.size() > 0) {
            selected = provisioningRequests.get(0)
            if (params.id != null) {
                selected = provisioningRequests.find {it.id == params.int('id')}
                render template: "showRequest", model:[filters: filters, selected: selected]
                return
            }
        }

        if (params.applyFilter || params.partial) {
            render template: "listRequests", model: [requests: provisioningRequests, filters: filters, selected: selected, totalCount: size]
        } else {
            render view: "showProvisioningRequest", model: [requests: provisioningRequests, filters: filters, selected: selected, totalCount: size]
        }

    }

    private def getFilteredCommands(filters) {
        params.max = (params?.max?.toInteger()) ?: pagination.max
        params.offset = (params?.offset?.toInteger()) ?: pagination.offset
        params.sort = params?.sort ?: pagination.sort
        params.order = params?.order ?: pagination.order

        def commands

        if (getParamType()) {
            commands = webServicesSession.getProvisioningCommands(getParamType(), (session.provisioningTypeId as int)) as List
        } else {
            def companyId = session['company_id'].toInteger()
            commands = companyId ? webServicesSession.getCommandsByEntityId(companyId) : null
        }
        filters.each { filter ->
            if (filter.value != null) {
                if(filter.field == 'id') {
                    commands = commands.findAll { it.getId() == filter.integerValue }
                }
                else if(filter.field == 'createDate') {
                    commands = commands.findAll { it.getCreateDate() >= filter.startDateValue && it.getCreateDate() <= filter.endDateValue }
                }
                else if(filter.field == 'commandStatus') {
                    commands = commands.findAll { it.getCommandStatus() == ProvisioningCommandDTO.ProvisioningCommandStatus.values()[filter.integerValue] }
                }
                else if(filter.field == 'commandType') {
                    commands = commands.findAll { it.getCommandType() == ProvisioningCommandType.values()[filter.integerValue] }
                }
            }
        }

        commands=commands.sort{-it."${params.sort}"}
        if (params.order=="desc"){
            commands=commands.reverse()
        }
        return commands
    }

    private def getFilteredRequests(filters) {
        params.max = (params?.max?.toInteger()) ?: pagination.max
        params.offset = (params?.offset?.toInteger()) ?: pagination.offset
        params.sort = params?.sort ?: pagination.sort
        params.order = params?.order ?: pagination.order

        def commands = null
        if (getParamType()) {
            commands = webServicesSession.getProvisioningCommands(getParamType(), (session.provisioningTypeId as int)) as List
        } else {
            def companyId = session['company_id'].toInteger()
            commands = companyId ? webServicesSession.getCommandsByEntityId(companyId) : null
        }
        def commandId = session.provisioningSelectedId as int

        filters.each { filter ->
            if (filter.value != null) {
                if(filter.field == 'provisioning.req_command_id') {
                    commandId = filter.stringValue as int
                }
            }
        }

        def requests = null
        if (commandId != null){
            requests = webServicesSession.getProvisioningRequests(commandId)
        }
        filters.each { filter ->
            if (filter.value != null) {
                if(filter.field == 'id') {
                    requests = requests.findAll { it.getId() == filter.integerValue }
                }
                else if(filter.field == 'provisioning.create_date') {
                    requests = requests.findAll { it.getCreateDate() >= filter.startDateValue && it.getCreateDate() <= filter.endDateValue }
                }
                else if(filter.field == 'provisioning.req_status') {
                    requests = requests.findAll { it.getRequestStatus() == ProvisioningRequestStatus.values()[filter.integerValue] }
                }
                else if(filter.field == 'provisioning.req_processor') {
                    requests = requests.findAll { it.getProcessor() == filter.stringValue }
                }
            }
        }
        return requests
    }

    def list (){

        params.max = params?.max?.toInteger() ?: pagination.max
        params.offset = params?.offset?.toInteger() ?: pagination.offset
        session.provisioningType = null
        session.provisioningShow == null

        try {

                showCommands()

        }
        catch (Exception ex) {
            ex.message
        }
    }

    private def store_params (){
        def type = params.type
        def typeId = params.typeId
        def selectedId = params.int('selectedId')

        if (type) session.provisioningType = type
        if (typeId) session.provisioningTypeId = typeId
        if (selectedId) session.provisioningSelectedId = selectedId
    }

    private def getParamType (){

        def paramType

        if (session.provisioningType == "ASSET") {
            paramType = ProvisioningCommandType.ASSET;
        } else if (session.provisioningType == "ORDER") {
            paramType = ProvisioningCommandType.ORDER;
        } else if (session.provisioningType == "ORDER_LINE") {
            paramType = ProvisioningCommandType.ORDER_LINE;
        } else if (session.provisioningType == "PAYMENT") {
            paramType = ProvisioningCommandType.PAYMENT;
        }
        paramType
    }

    private def getObjectDTO(typeId = -1) {

        def das = null

        if (session.provisioningType == "ASSET") {
            das = new AssetDAS();
        } else if (session.provisioningType == "ORDER") {
            das = new OrderDAS();
        } else if (session.provisioningType == "ORDER_LINE") {
            das = new OrderLineDAS();
        } else if (session.provisioningType == "PAYMENT") {
            das = new PaymentDAS();
        }

        def objDTO = typeId == -1 ? das.findNow(session.provisioningTypeId as int) : das.findNow(typeId)

        objDTO
    }
}
