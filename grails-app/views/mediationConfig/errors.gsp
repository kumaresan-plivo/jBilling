<%@ page import="com.sapienter.jbilling.server.item.PricingField" %>
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
            $('#first').val('true');
            $('#order-events-form').submit();
        }
    </script>
</head>
<body>

    <g:if test="${mediationConfiguration}">
        <div class="table-info" >
            <em>
                <g:message code="event.error.record.mediation.config.id"/>
                <strong>${mediationConfiguration?.id}</strong>
            </em>
            <em>
                <g:message code="event.error.record.status.id"/>
                <strong><g:formatNumber number="${record?.recordStatusId}"/></strong>
            </em>
            <g:if test="${errorCodes}">
                 <em>
                     <g:message code="event.error.record.errorCodes"/>
                     <strong>${errorCodes}</strong>
                 </em>
             </g:if>
        </div>

        <div class="table-area" style="overflow:scroll;font-size: 10px;padding: 0px 0px;white-space: nowrap;">
            <table>
                <thead>
                    <tr>
                        <td class="first"><g:message code="event.error.th.id"/></td>
                        <td><g:message code="event.error.th.key"/></td>
                        <td ><g:message code="event.error.processId"/></td>
                        <td><g:message code="event.error.processing.date" default="Processing Date"/></td>
                        <g:if test="${record}">
                            <g:each var="field" in="${record.fields}">
                                <td>${field.name}</td>
                            </g:each>
                        </g:if>
                        <td class="last"><g:message code="event.error.th.codes"/></td>
                    </tr>
                </thead>
                <tbody>


                <g:each var="recordLine" in="${records}">
                    <tr>
                        <td class="col02">
                            ${recordLine.recordId.toString().replaceAll(" ", "&nbsp;")}
                        </td>
                        <td>
                            ${recordLine.key.toString().replaceAll(" ", "&nbsp;")}
                        </td>
                        <td>
                            ${recordLine.getMediationProcessId()}
                        </td>
                        <td>
                           <g:formatDate formatName="default.date.format" date="${recordLine.getProcessingDate()}" />
                        </td>

                        <g:each var="pricingField" in="${recordLine.fields}">
                            <td>
                                <g:if test="${pricingField.type==PricingField.Type.DATE}">
                                    <g:formatDate date="${pricingField.value}" formatName="default.date.format"/>
                                </g:if>
                            <g:else>
                                ${pricingField.value.toString().replaceAll(" ", "&nbsp;")}
                            </g:else>
                            </td>
                        </g:each>

                        <td>
                            <strong>
                                ${recordLine.errors.toString().replaceAll(" ", "&nbsp;")}
                            </strong>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </div>
        <div class="form-hold">
                <g:form name="order-events-form" controller="mediationConfig" action="${params.action}" id="${params?.id}">
                    <fieldset>
                        <div class="form-columns">
                            <g:hiddenField name="offset" value="${offset}"/>
                            <div class="row">
                                <g:applyLayout name="form/date">
                                    <content tag="label"><g:message code="event.summary.start.date"/></content>
                                    <content tag="label.for">startDate</content>
                                    <g:textField class="field " name="startDate" value="${params.startDate}" onblur="validateDate(this)"/>
                                </g:applyLayout>
                                <g:applyLayout name="form/date">
                                    <content tag="label"><g:message code="event.summary.end.date"/></content>
                                    <content tag="label.for">endDate</content>
                                    <g:textField class="field" name="endDate" value="${params.endDate}" onblur="validateDate(this)"/>
                                </g:applyLayout>
                                <g:hiddenField name="first" value="false"/>
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
       </g:if>
<div class="pager-box">
    <div class="row">
        <div class="download">
            <sec:access url="/mediation/mediationErrorsCsv">
                <g:link action="mediationErrorsCsv" id="${mediationConfiguration?.id}"
                        params="${params + ['status': record?.recordStatusId, errorCodes: errorCodes?.join(':')]}">
                    <g:message code="download.csv.link"/>
                </g:link>
            </sec:access>
        </div>
    </div>
</div>
</body>
</html>
