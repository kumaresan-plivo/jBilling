%{--
  JBILLING CONFIDENTIAL
  _____________________

  [2003] - [2012] Enterprise jBilling Software Ltd.
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Enterprise jBilling Software.
  The intellectual and technical concepts contained
  herein are proprietary to Enterprise jBilling Software
  and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden.
  --}%

<%@ page import="org.apache.commons.lang.StringEscapeUtils; com.sapienter.jbilling.server.user.contact.db.ContactDTO"%>

<%-- 
    Orders list template. 
    
    @author Vikas Bodani
    @since 20-Jan-2011
 --%>

<div class="table-box">
    <div class="table-scroll">
        <table id="orders" cellspacing="0" cellpadding="0">
            <thead>
                <tr>
                    <th class="small">
                        <g:remoteSort action="list" sort="id" update="column1">
                            <g:message code="order.label.id"/>
                        </g:remoteSort>
                    </th>
                    <th class="large">
                        <g:remoteSort action="list" sort="contact.firstName, contact.lastName, contact.organizationName, u.userName" alias="[contact: 'baseUserByUserId.contact']" update="column1">
                            <g:message code="order.label.customer"/>
                        </g:remoteSort>
                    </th>
                    <g:isRoot>
                		<th class="small">
                			<g:remoteSort action="list" sort="company.description" alias="[company: 'baseUserByUserId.company']" update="column1">
                    	    	<g:message code="order.label.company"/>
                    		</g:remoteSort>
                		</th>
                	</g:isRoot>
                    <th class="small">
                        <g:remoteSort action="list" sort="createDate" update="column1">
                            <g:message code="order.label.date"/>
                        </g:remoteSort>
                    </th>
                    <th class="small">
                        <g:message code="order.label.amount"/>
                    </th>
                    <th class="tiny3">
                        <g:message code="order.label.parent.child"/>
                    </th>
                </tr>
            </thead>
            <tbody>
                <g:each var="ordr" in="${orders}">
                    <g:set var="contact" value="${ContactDTO.findByUserId(ordr?.baseUserByUserId?.id)}"/>
                    <tr id="order-${ordr.id}" class="${(order?.id == ordr?.id) ? 'active' : ''}">
                        <td>
                            <g:remoteLink breadcrumb="id" class="cell" action="show" id="${ordr.id}" params="['template': 'show']" before="register(this);" onSuccess="render(data, next);">
                                ${ordr.id}
                            </g:remoteLink>
                        </td>
                        <td>
                            <g:remoteLink breadcrumb="id" class="double cell" action="show" id="${ordr.id}" params="['template': 'show']" before="register(this);" onSuccess="render(data, next);">
                                <strong>
                                    <g:if test="${contact?.firstName || contact?.lastName}">
                                        ${StringEscapeUtils.escapeHtml(contact?.firstName)} &nbsp;${StringEscapeUtils.escapeHtml(contact?.lastName)}
                                    </g:if> 
                                    <g:else>
                                        ${StringEscapeUtils.escapeHtml(ordr?.baseUserByUserId?.userName)}
                                    </g:else>
                                </strong>
                                <em>${StringEscapeUtils.escapeHtml(contact?.organizationName)}</em>
                            </g:remoteLink>
                        </td>
                        <g:isRoot>
                        	<td>
                				<g:remoteLink breadcrumb="id" class="cell" action="show" id="${ordr.id}" params="['template': 'show']" before="register(this);" onSuccess="render(data, next);">
                                	<strong>${StringEscapeUtils.escapeHtml(ordr?.baseUserByUserId?.company?.description)}</strong>
                            	</g:remoteLink>
                			</td>
                		</g:isRoot>
                        <td>
                            <g:remoteLink breadcrumb="id" class="cell" action="show" id="${ordr.id}" params="['template': 'show']" before="register(this);" onSuccess="render(data, next);">
                                <g:formatDate date="${ordr?.createDate}" formatName="date.pretty.format"/>
                            </g:remoteLink>
                        </td>
                        <td>
                            <g:remoteLink breadcrumb="id" class="cell" action="show" id="${ordr.id}" params="['template': 'show']" before="register(this);" onSuccess="render(data, next);">
                                <g:formatNumber number="${ordr?.total}" type="currency" currencySymbol="${ordr?.currency?.symbol}"/>
                            </g:remoteLink>
                        </td>
                        <td class="center">
                            <g:if test="${ordr}">
                                <g:set var="childrenSize" value="${children[ordr.id] != null ? children[ordr.id] : 0}"/>
                                <g:set var="isParent" value="${childrenSize > 0}"/>
                                <g:if test="${isParent && ordr.parentOrder}">
                                    <%-- is a parent, but also a child of another order --%>
                                    <g:remoteLink action="suborders" id="${ordr.id}" before="register(this);" onSuccess="render(data, next);">
                                        <img src="${resource(dir:'images', file:'icon17.gif')}" alt="parent and child" />
                                        <span>${childrenSize}</span>
                                    </g:remoteLink>
                                </g:if>
                                <g:elseif test="${isParent && !ordr.parentOrder}">
                                    <%-- is a top level parent --%>
                                    <g:remoteLink action="suborders" id="${ordr.id}" before="register(this);" onSuccess="render(data, next);">
                                        <img src="${resource(dir:'images', file:'icon18.gif')}" alt="parent" />
                                        <span>${childrenSize}</span>
                                    </g:remoteLink>
                                </g:elseif>
                                <g:elseif test="${!isParent && ordr.parentOrder}">
                                    <%-- is a child order, but not a parent --%>
                                    <img src="${resource(dir:'images', file:'icon19.gif')}" alt="child" />
                                </g:elseif>
                            </g:if>
                        </td>
                    </tr>
                </g:each>
            </tbody>
        </table>
     </div>
</div>

<g:if test="${!parent}">
<div class="pager-box">
    <div class="row">
        <div class="results">
            <g:render template="/layouts/includes/pagerShowResults" model="[steps: [10, 20, 50], update: 'column1', ids: ids, id: params?.id, action: params?.action]"/>
        </div>
        <div class="download">
            <sec:access url="/order/csv">
                <g:link action="csv" id="${order?.id}">
                    <g:message code="download.csv.link"/>
                </g:link>
            </sec:access>
        </div>
    </div>

    <div class="row">
            <util:remotePaginate controller="order" action="${params.action}" id="${params?.id}" params="${sortableParams(params: [partial: true])}" total="${orders?.totalCount ?: 0}" update="column1"/>
    </div>
</div>

<div class="btn-box">
    <div class="row">
        <sec:ifAnyGranted roles="ROLE_CUSTOMER,ROLE_PARTNER">
            <sec:ifAllGranted roles="ORDER_20">
                <g:link controller="orderBuilder" action="edit" params="[userId: session['user_id']]" class="submit order"><span><g:message code="button.create.order"/></span></g:link>
            </sec:ifAllGranted>
        </sec:ifAnyGranted>
    </div>
</div>
</g:if>
