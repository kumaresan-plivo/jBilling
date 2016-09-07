<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
   <div class="table-box">
       <div class="table-scroll">
           <table id="requests" cellspacing="0" cellpadding="0">
               <thead>
               <tr>
                    <g:isRoot>
                        <th class="medium"><g:message code="provisioning.request.label.id"/></th>
                    </g:isRoot>
                    <th class="medium"><g:message code="provisioning.request.label.processor"/></th>
                    <th class="medium"><g:message code="provisioning.request.label.status"/></th>
                    <th class="medium"><g:message code="provisioning.request.label.execution_order"/></th>
                </tr>
                </thead>
                <tbody>
                <g:each var="req" in="${requests}">
                    <tr id="req-${req.id}" class="${selected?.id == req.id ? 'active' : ''}">
                        <td class="medium">
                            <g:remoteLink class="cell" id="${req.id}" action="showRequests" before="register(this);" onSuccess="render(data, next);">
                                <span>${req.id}</span>
                            </g:remoteLink>
                        </td>
                        <td class="medium">
                            <g:remoteLink class="cell" id="${req.id}" action="showRequests" before="register(this);" onSuccess="render(data, next);">
                                <span>${StringEscapeUtils.escapeHtml(req?.processor)}</span>
                            </g:remoteLink>
                        </td>
                        <td class="medium">
                            <g:remoteLink class="cell" id="${req.id}" action="showRequests" before="register(this);" onSuccess="render(data, next);">
                                <span>${req.requestStatus}</span>
                            </g:remoteLink>
                        </td>
                        <td class="medium">
                            <g:remoteLink class="cell" id="${req.id}" action="showRequests" before="register(this);" onSuccess="render(data, next);">
                                <span>${req.executionOrder}</span>
                            </g:remoteLink>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </div>
    </div>
<div class="pager-box">
    <div class="results">
        <g:message code="pager.show.max.results"/>
        <g:each var="max" in="${[10, 20, 50]}">
            <g:if test="${params.max == max}">
                <span>${max}</span>
            </g:if>
            <g:else>
                <g:remoteLink controller="provisioning" action="callRequestsList" before="register(this);" onSuccess="render(data, current);" params="[max: max]">${max}</g:remoteLink>
            </g:else>
        </g:each>
    </div>
    <div class="row">
        <util:remotePaginate controller="provisioning" action="callRequestsList" before="register(this);" onSuccess="render(data, current);" params="[max: params.max]"
                             total="${totalCount?totalCount:0}"
                             update="column1"/>
    </div>
</div>