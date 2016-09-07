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
        function showEntitySpecificRecords(){
            var selected=$('#company-select').val();
            document.location.href = '${createLink(controller: 'mediation' , action:'showMediationErrors')}?selectedEntity=' + selected+'&id=${params.id}'+'&status=${params.status}';
        }
    </script>
</head>
<body>


        <div class="table-info" >
            <em>
                <g:message code="event.error.record.mediation.id"/>
                <strong>${params.id}</strong>
            </em>
            <em>
                <g:message code="event.error.record.status.id"/>
                <strong><g:formatNumber number="${params?.status}"/></strong>
            </em>
            <g:isRoot>
                <em>
                    <g:select id="company-select" name="product.entities" from="${companies}"
                              optionKey="id" optionValue="${{it?.description?.decodeHTML()}}"
                              value="${selected}"
                              onChange="showEntitySpecificRecords()"/>
                </em>
            </g:isRoot>
        </div>
    <g:if test="${record}">
        <div class="table-area" style="overflow:scroll;font-size: 10px;padding: 0px 0px;white-space: nowrap;">
            <table>
                <thead>
                    <tr>
                        <td class="first"><g:message code="event.error.th.id"/></td>
                        <td><g:message code="event.error.th.key"/></td>
                        <g:if test="${record}">
                            <g:each var="field" in="${PricingField.getPricingFieldsValue(record.pricingFields)}">
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
                            ${recordLine.jBillingCompanyId} - ${recordLine.mediationCfgId} - ${recordLine.recordKey}
                        </td>
                        <td>
                            ${recordLine.recordKey}
                        </td>
                        <g:each var="field" in="${PricingField.getPricingFieldsValue(recordLine.pricingFields)}">
                            <td>${field.value}</td>
                        </g:each>
                        <td>
                            <strong>
                                ${recordLine.errorCodes}
                            </strong>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </div>
        <div class="form-hold">
            <g:form name="order-events-form" controller="mediation" action="showMediationErrors" id="${params?.id}">
                <fieldset>
                    <div class="form-columns">
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
        <div class="pager-box">
            <div class="row">
                <div class="download">
                    <sec:access url="/mediation/mediationErrorsCsv">
                        <g:link action="mediationErrorsCsv" id="${record?.processId}"
                                params="${params}">
                            <g:message code="download.csv.link"/>
                        </g:link>
                    </sec:access>
                </div>
            </div>
        </div>

    </g:if>

</body>
</html>
