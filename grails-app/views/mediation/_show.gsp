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

<%@ page import="org.joda.time.Period; com.sapienter.jbilling.server.util.Constants;" %>

<%--
    @author Vikas Bodani, Pance Isajeski
    @since 18 Feb 2011
 --%>

<div class="column-hold">
    <div class="heading">
        <strong><g:message code="mediation.process.title"/> <em>${selected.id}</em>
        </strong>
    </div>
 
    <div class="box">
        <div class="sub-box">
            <!-- mediation process info -->
            <table cellspacing="0" cellpadding="0" class="dataTable">
                <tbody>
                    <tr>
                        <td><g:message code="mediation.label.id"/></td>
                        <td class="value">${selected.id}</td>
                    </tr>
                    <tr>
                        <td><g:message code="mediation.label.config"/></td>
                        <td class="value">${selected.configurationId}</td>
                    </tr>
                    <tr>
                        <td><g:message code="mediation.label.start.time"/></td>
                        <td class="value"><g:formatDate date="${selected.startDate}" formatName="date.timeSecsAMPM.format"/></td>
                    </tr>
                    <tr>
                        <td><g:message code="mediation.label.end.time"/></td>
                        <td class="value"><g:formatDate date="${selected.endDate}" formatName="date.timeSecsAMPM.format"/></td>
                    </tr>
                    <tr>
                        <td><g:message code="mediation.label.total.runtime"/></td>
                        <td class="value">
                            <g:if test="${selected.startDate && selected.endDate}">
                                <g:set var="runtime" value="${new Period(selected.startDate?.time, selected.endDate?.time)}"/>
                                <g:message code="mediation.runtime.format" args="[runtime.getHours(), runtime.getMinutes(), runtime.getSeconds()]"/>
                            </g:if>
                            <g:else>
                                -
                            </g:else>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

    <!-- records info -->
    <div class="heading">
        <strong><g:message code="mediation.process.records"/></strong>
    </div>

    <div class="box">
        <div class="sub-box">
            <table cellpadding="0" cellspacing="0" class="dataTable">
                <tbody>
                    <tr>
                        <td><g:message code="mediation.label.orders.affected"/></td>
                        <td class="value">${ordersCreatedCount}</td>
                        <g:if test="${ordersCreatedCount > 0}">
                            <td class="value">
                                <sec:access url="/order/list">
                                    <g:link controller="order" action="byMediation" id="${selected.id}">
                                        <g:message code="mediation.show.all.orders"/>
                                    </g:link>
                                </sec:access>
                            </td>
                        </g:if>
                    </tr>
                    <tr>
                        <td><g:message code="mediation.label.invoices.created"/></td>
                        <td class="value">${invoicesCreatedCount}</td>
                        <g:if test="${invoicesCreatedCount > 0}">
                            <td class="value">
                                <sec:access url="/invoice/list">
                                    <g:link controller="invoice" action="byMediation" id="${selected.id}">
                                        <g:message code="mediation.show.all.invoices"/>
                                    </g:link>
                                </sec:access>
                            </td>
                        </g:if>
                    </tr>
                </tbody>
            </table>

            <hr/>

            <!-- mediation process stats -->
            <table cellspacing="0" cellpadding="0" class="dataTable">
                <tbody>
                    <tr>
                        <td><g:message code="mediation.label.done.billable"/></td>
                        <td class="value">${selected.doneAndBillable}</td>
                        <g:if test="${selected?.doneAndBillable != 0}">
                            <td class="value">
                                <sec:access url="/invoice/list">
                                    <g:link controller="mediation" action="showMediationRecords" id="${selected.id}"
                                            params="${params + ['status': Constants.MEDIATION_RECORD_STATUS_DONE_AND_BILLABLE]}">
                                        <g:message code="mediation.show.all.records"/>
                                    </g:link>
                                </sec:access>
                            </td>
                        </g:if>
                    </tr>
                    <tr>
                        <td><g:message code="mediation.label.errors.detected"/></td>
                        <td class="value">${selected.errors}</td>
                        <g:if test="${selected?.errors != 0}">
                            <td class="value">
                                <sec:access url="/invoice/list">
                                    <g:link controller="mediation" action="showMediationErrors" id="${selected.id}"
                                            params="${params + ['status': Constants.MEDIATION_RECORD_STATUS_ERROR_DETECTED]}">
                                        <g:message code="mediation.show.all.records"/>
                                    </g:link>
                                </sec:access>
                            </td>
                        </g:if>
                    </tr>
                     <tr class="column-hold">
                        <td class="col01"><g:message code="mediation.label.duplicate.records"/></td>
                        <td class="value">${selected?.duplicates ?:0}</td>
                        <g:if test="${selected?.errors != 0}">
                            <td class="value">
                                <sec:access url="/invoice/list">
                                    <g:link controller="mediation" action="showMediationDuplicates" id="${selected.id}"
                                            params="${params + ['status': Constants.MEDIATION_RECORD_STATUS_ERROR_DETECTED]}">
                                        <g:message code="mediation.show.all.records"/>
                                    </g:link>
                                </sec:access>
                            </td>
                        </g:if>
                    </tr>
                    <tr>
                    <tr class="column-hold">
                        <td class="col01"><g:message code="mediation.label.records"/></td>
                        <td class="value">${selected?.recordsProcessed}</td>
                    </tr>

                </tbody>
            </table>
        </div>
    </div>
    <div class="btn-box">
        <g:if test="${canBeUndone}">
            <a onclick="showConfirm('undo-${selected.id}');" class="submit delete">
                <span><g:message code="mediation.process.undo"/></span>
            </a>
        </g:if>
        <g:isRoot>
           <g:if test="${!isMediationProcessRunning && (selected.errors > 0 || selected.errors > 0)}">
               <g:link  class="submit" id="${selected.id}" action="recycleProcessCDRs">
                  <span ><g:message code="button.recycle.process"/></span>
               </g:link>
           </g:if>
        </g:isRoot>
        <g:if test="${canBeUndone}">

            <g:remoteLink  class="submit" id="${selected.id}" action="refreshMediationCounter" update="column2" params="[template: 'show']">
                <span><g:message code="button.refresh.process"/></span>
            </g:remoteLink>
        </g:if>

    </div>

    <g:render template="/confirm"
              model="['message': 'mediation.undo.confirm',
                      'controller': 'mediation',
                      'action': 'undo',
                      'id': selected?.id,
                      'ajax': false,
              ]"/>
</div>
