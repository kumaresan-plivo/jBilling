<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
    <div class="table-box">
        <div class="table-scroll">
            <table id="invoices" cellspacing="0" cellpadding="0">
                <thead>
                <tr>
                    <th class="medium">
                        <g:remoteSort action="showCommands" sort="name" update="column1">
                            <g:message code="provisioning.label.name"/>
                        </g:remoteSort>
                    </th>
                    <th class="medium">
                        <g:remoteSort action="showCommands" sort="commandStatus" update="column1">
                            <g:message code="provisioning.label.status"/>
                        </g:remoteSort>
                    </th>
                    <g:isRoot>
                        <th class="medium">
                            <g:remoteSort action="showCommands" sort="commandType" update="column1">
                                <g:message code="provisioning.label.type"/>
                            </g:remoteSort>
                        </th>
                    </g:isRoot>
                    <th class="medium">
                        <g:remoteSort action="showCommands" sort="createDate" update="column1">
                            <g:message code="provisioning.label.date"/>
                        </g:remoteSort>
                    </th>
                </tr>
                </thead>

                <tbody>
                <g:each var="cmd" in="${commands}">
                    <tr id="req-${cmd.id}" class="${selected?.id == cmd.id ? 'active' : ''}">
                        <td class="medium">
                            <g:remoteLink class="cell" id="${cmd.id}" action="showCommands" before="register(this);" onSuccess="render(data, next);"  params="[show: true]">
                                <span>${StringEscapeUtils.escapeHtml(cmd?.name)}</span>
                            </g:remoteLink>
                        </td>
                        <td class="medium">
                            <g:remoteLink class="cell" id="${cmd.id}" action="showCommands" before="register(this);" onSuccess="render(data, next);" params="[show: true]">
                                <span>${cmd.commandStatus}</span>
                            </g:remoteLink>
                        </td>
                        <td class="medium">
                            <g:remoteLink class="cell" id="${cmd.id}" action="showCommands" before="register(this);" onSuccess="render(data, next);" params="[show: true]">
                                <span>${cmd.commandType}</span>
                            </g:remoteLink>
                        </td>
                        <td class="medium">
                            <g:remoteLink class="cell" id="${cmd.id}" action="showCommands" before="register(this);" onSuccess="render(data, next);" params="[show: true]">
                                <g:formatDate date="${cmd.createDate}" formatName="date.pretty.format"/>
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
                <g:remoteLink controller="provisioning" action="callCommandsList" before="register(this);" onSuccess="render(data, current);" params="[max: max]">${max}</g:remoteLink>
            </g:else>
        </g:each>
    </div>
    <div class="row">
        <util:remotePaginate controller="provisioning" action="callCommandsList" before="register(this);" onSuccess="render(data, current);" params="${sortableParams(params: [partial: true, max: params.max])}"
                             total="${totalCount?totalCount:0}"
                             update="column1"/>
    </div>
</div>
