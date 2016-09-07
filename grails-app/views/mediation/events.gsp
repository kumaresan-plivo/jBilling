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

<%@ page import="com.sapienter.jbilling.server.user.db.CompanyDTO" %>

<%
    def company = CompanyDTO.load(session['company_id'])
    def childEntities = CompanyDTO.findAllByParent(company)
    def allEntities = childEntities + company
%>

<html>
<head>
	<meta name="layout" content="main"/>
	<script type="text/javascript">
        function validateDate(element) {
            var dateFormat= "<g:message code="date.format"/>";
            if(!isValidDate(element, dateFormat)) {
                $("#error-messages").css("display","block");
                $("#error-messages ul").css("display","block");
                $("#error-messages ul").html("<li><g:message code="invalid.date.format"/></li>");
                element.focus();
                return false;
            } else {
                return true;
            }
        }

        function submitForFirst() {
            //alert($('#selectionEntityId').val());
        	$('#first').val('true');
        	$('#order-events-form').submit();
        }
        function showEntitySpecificRecords(){
            var selected=$('#selectionEntityId').val();
            document.location.href = '${createLink(controller: 'mediation' , action:'showMediationRecords')}?entityId=' + selected+'&id=${processId}'+'&status=${params.status}';
        }
    </script>
</head>
<body>
    <g:set var="currency" value="${invoice?.currency ?: order?.currency ?: currency}"/>

    %{-- Invoice summary if invoice set --}%
    <g:if test="${invoice}">
        <div class="table-info" >
            <em>
                <g:message code="event.summary.invoice.id"/>
                <strong>${invoice.id}</strong>
            </em>
            <em>
                <g:message code="event.summary.invoice.due.date"/>
                <strong><g:formatDate date="${invoice.dueDate}" formatName="date.pretty.format"/></strong>
            </em>
            <em>
                <g:message code="event.summary.invoice.total"/>
                <strong><g:formatNumber number="${invoice.total}" type="currency" currencySymbol="${currency?.symbol}"/></strong>
            </em>
        </div>
    </g:if>

    %{-- Order summary if order set --}%
    <g:if test="${order}">
        <div class="table-info" >
            <em>
                <g:message code="event.summary.order.id"/>
                <strong>${order.id}</strong>
            </em>
            <em>
                <g:message code="event.summary.order.total"/>
                <strong><g:formatNumber number="${order.total}" type="currency" currencySymbol="${currency?.symbol}"/></strong>
            </em>
        </div>
    </g:if>

    %{-- Record summary set --}%

        <div class="table-info" >
            <em>
                <g:message code="event.summary.record.mediation.id"/>
                <strong>${processId}</strong>
            </em>
            <em>
                <g:message code="event.summary.record.status.id"/>
                <strong>${params?.status}</strong>
            </em>
            <g:if test="${ null == company.getParent()}">
                <em>
                    <g:message code="event.summary.record.entity.id"/>
                    <strong>
                        <g:select id="selectionEntityId" name="selectionEntityId"
                            from="${allEntities}"
                            optionKey="id"
                            optionValue="${{it?.description?.decodeHTML()}}"
                            value="${selectionEntityId}"
                            onchange="showEntitySpecificRecords();"
                            />
                    </strong>
                </em>
            </g:if>
        </div>

<g:if test="${record}">
    <div class="table-area">
        <table>
            <thead>
                <tr>
                    <td class="first"><g:message code="event.th.id"/></td>
                    <td><g:message code="event.th.key"/></td>
                    <td><g:message code="event.th.date"/></td>
                    <td><g:message code="event.th.description"/></td>
                    <td><g:message code="event.th.quantity"/></td>
                    <td class="last"><g:message code="event.th.cost.amount"/></td>
                    <td class="last"><g:message code="event.th.amount"/></td>
                </tr>
            </thead>
            <tbody>

            <!-- events list -->
            <g:set var="totalQuantity" value="${BigDecimal.ZERO}"/>
            <g:set var="totalAmount" value="${BigDecimal.ZERO}"/>
            <g:set var="totalCostAmount" value="${BigDecimal.ZERO}"/>

            <g:each var="recordLine" in="${records}">
                <g:if test="${recordLine?.quantity}" >
                    <g:set var="totalQuantity" value="${totalQuantity.add(recordLine.quantity)}"/>
                </g:if>
                <g:if test="${recordLine?.ratedPrice}" >
                    <g:set var="totalAmount" value="${totalAmount.add(recordLine.ratedPrice)}"/>
                </g:if>
                <g:if test="${totalCostAmount}" >
                    <g:set var="totalCostAmount" value="${totalAmount.add(recordLine.ratedCostPrice)}"/>
                </g:if>
                <tr>
                    <td class="col02">
                        ${recordLine.recordKey} - ${recordLine.eventDate}
                    </td>
                    <td>
                        ${recordLine.recordKey}
                    </td>
                    <td>
                        <g:formatDate date="${recordLine.eventDate}" formatName="date.pretty.format"/>
                    </td>
                    <td class="col03">
                        ${recordLine.description ?: '-'}
                    </td>
                    <td>
                        <strong>
                            <g:formatNumber number="${recordLine.quantity}" formatName="decimal.format"/>
                        </strong>
                    </td>
                    <td>
                        <strong>
                            <g:formatNumber number="${recordLine.ratedCostPrice}" type="currency" currencySymbol="${currency?.symbol}"/>
                        </strong>
                    </td>
                    <td>
                        <strong>
                            <g:formatNumber number="${recordLine.ratedPrice}" type="currency" currencySymbol="${currency?.symbol}"/>
                        </strong>
                    </td>
                </tr>
            </g:each>

                <!-- subtotals -->
                <tr class="bg">
                    <td class="col02"></td>
                    <td></td>
                    <td></td>
                    <td></td>

                    <td>
                        <strong><g:formatNumber number="${totalQuantity}" formatName="decimal.format"/></strong>
                    </td>
                    <td>
                        <strong><g:formatNumber number="${totalCostAmount}" type="currency" currencySymbol="${currency?.symbol}"/></strong>
                    </td>
                    <td>
                        <strong><g:formatNumber number="${totalAmount}" type="currency" currencySymbol="${currency?.symbol}"/></strong>
                    </td>
                </tr>

            </tbody>
        </table>
    </div>

	<div class="form-hold">
	        <g:form name="order-events-form" controller="mediation" action="${params.action}" id="${processId}">
	            <fieldset>
	                <div class="form-columns">
						<div class="row">
							<g:applyLayout name="form/date">
						          <content tag="label"><g:message code="event.summary.start.date"/></content>
						          <content tag="label.for">event_start_date</content>
						          <g:textField class="field" name="event_start_date" value="${formatDate(date: params?.event_start_date?new Date(params?.event_start_date):null, formatName:'datepicker.format')}" onblur="validateDate(this)"/>
						     </g:applyLayout>
						     		<g:applyLayout name="form/date">
						          <content tag="label"><g:message code="event.summary.end.date"/></content>
						          <content tag="label.for">event_end_date</content>
						          <g:textField class="field" name="event_end_date" value="${formatDate(date: params?.event_end_date?new Date(params?.event_end_date):null, formatName:'datepicker.format')}" onblur="validateDate(this)"/>
						     </g:applyLayout>
						     <g:hiddenField name="first" value="false"/>
						     <g:hiddenField name="status" value="${record?.status}"/>
                             <g:hiddenField name="offset" value="${params.offset}"/>
                             <g:hiddenField name="entityId" value="${selectionEntityId}"/>
					    </div>

						<div class="row">
							<a onclick="submitForFirst();" class="submit show">
								<span><g:message code="button.view.first.events" /></span>
							</a>
							<a onclick="$('#order-events-form').submit();" class="submit show">
								<span><g:message code="button.view.next.events"/></span>
							</a>
					   </div>
				   </div>
			   </fieldset>
		   </g:form>
	</div>

        %{--<div class="pager-box">--}%
            %{--<div class="row">--}%
                %{--<div class="download">--}%
                    %{--<g:if test="${orderEvents}">--}%
                        %{--<sec:access url="/mediation/orderRecordsCsv">--}%
                            %{--<g:link action="orderRecordsCsv" id="${order.id}">--}%
                                %{--<g:message code="download.csv.link"/>--}%
                            %{--</g:link>--}%
                        %{--</sec:access>--}%
                    %{--</g:if>--}%
                    %{--<g:else>--}%
                        %{--<sec:access url="/mediation/mediationRecordsCsv">--}%
                            %{--<g:link action="mediationRecordsCsv" id="${record?.processId}"--}%
                                    %{--params="${params + ['status': record?.status]}">--}%
                                %{--<g:message code="download.csv.link"/>--}%
                            %{--</g:link>--}%
                        %{--</sec:access>--}%
                    %{--</g:else>--}%
                %{--</div>--}%
            %{--</div>--}%
        %{--</div>--}%

</g:if>
</body>
</html>
