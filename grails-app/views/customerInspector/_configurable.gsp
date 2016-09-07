%{--
  JBILLING CONFIDENTIAL
  _____________________

  [2003] - [2015] Enterprise jBilling Software Ltd.
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Enterprise jBilling Software.
  The intellectual and technical concepts contained
  herein are proprietary to Enterprise jBilling Software
  and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden.
  --}%

<%--
  Configurable Customer Information Screen.
--%>

<%@ page import="com.sapienter.jbilling.server.payment.PaymentWS; com.sapienter.jbilling.server.invoice.InvoiceWS; com.sapienter.jbilling.server.order.OrderWS; java.lang.reflect.Array; com.sapienter.jbilling.server.customerInspector.domain.*; org.apache.commons.lang.StringUtils; org.apache.commons.lang.math.NumberUtils"%>

<style>
    .element {
        width: 320px;
        max-width: 320px;
        display: inline-block;
    }
    .element2 {
        width: 320px;
        max-width: 320px;
        display: inline-block;
    }
    .fixed_headers {
        width: 100%;
        table-layout: fixed;
        border-collapse: collapse;
    }
    .fixed_headers th {
        text-decoration: underline;
    }
    .fixed_headers th,
    .fixed_headers td {
        text-align: center;
    }
    .fixed_headers td:nth-child(1),
    .fixed_headers th:nth-child(1) {
        min-width: 100px;
    }
    .fixed_headers td:nth-child(2),
    .fixed_headers th:nth-child(2) {
        min-width: 100%;
    }
    .fixed_headers td:nth-child(3),
    .fixed_headers th:nth-child(3) {
        width: 100%;
    }
    .fixed_headers thead {
        background-color: #3586FF;
        color: #fdfdfd;
    }
    .fixed_headers thead tr {
        display: block;
        position: relative;
        color: #fdfdfd;
    }
    .fixed_headers tbody {
        display: block;
        overflow: auto;
        width: 100%;
        height: 100px;
    }
    .fixed_headers tbody tr:nth-child(even) {
        background-color: #dddddd;
    }
    .old_ie_wrapper {
        height: 100px;
        width: 100%;
        overflow-x: hidden;
        overflow-y: auto;
    }
    .old_ie_wrapper tbody {
        height: auto;
    }
</style>

<div class="dataTable">
    <g:each var="row" in="${customerInformation?.rows}">
        <div class="box-cards" >
            <g:each var="column" in="${row?.columns}">
                <g:if test="${null!=column && column?.field}">
                    <g:if test="${column?.field instanceof ListField}">
                        <div class="element" style="${column?.field?.style}">
                            <table id="listField" class="fixed_headers">
                                <g:set var="listFieldProperties" value="${column?.field?.properties}"/>
                                <g:set var="labelsList"
                                       value="${null!=column?.field?.getLabels() && column?.field?.getLabels().size()==listFieldProperties.size()  ? column?.field?.getLabels() : null}"/>
                                <thead>
                                    <tr>
                                        <g:set var="propertiesTotal" value="${listFieldProperties.size()}"/>
                                        <g:each var="listFieldProperty" in="${listFieldProperties}" status="i">
                                            <g:if test="${labelsList}">
                                                <th align="center" style="width: ${300.intdiv(propertiesTotal)}px;">${StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(labelsList[i]), ' ').capitalize()}</th>
                                            </g:if>
                                            <g:else>
                                                <th align="center" style="width: ${300.intdiv(propertiesTotal)}px;">${StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(listFieldProperty), ' ').capitalize()}</th>
                                            </g:else>
                                        </g:each>
                                    </tr>
                                </thead>
                                <tbody>
                                <g:each var="element" in="${column?.field.getValue(user?.id)}">
                                    <tr>
                                        <g:each var="listFieldProperty" in="${listFieldProperties}">
                                            <g:if test="${column?.field.getType().equals(ListField.Type.ORDER)}">
                                                <g:set var="classObject" value="${OrderWS.class}"/>
                                            </g:if>
                                            <g:elseif test="${column?.field.getType().equals(ListField.Type.INVOICE)}">
                                                <g:set var="classObject" value="${InvoiceWS.class}"/>
                                            </g:elseif>
                                            <g:else>
                                                <g:set var="classObject" value="${PaymentWS.class}"/>
                                            </g:else>
                                            <g:if test="${column?.field.isValidProperty(listFieldProperty,classObject)}">
                                                <g:set var="listFieldPropertyValue" value='${fieldValue(bean: element, field: listFieldProperty)}'/>
                                                <td align="center" style="width: 150px;">
                                                    <g:if test="${listFieldPropertyValue.isNumber()}">
                                                        <g:if test="${column?.field.isMoneyProperty(listFieldProperty)}">
                                                            $
                                                        </g:if>
                                                        <g:formatNumber number="${listFieldPropertyValue}" maxFractionDigits="2"/>
                                                    </g:if>
                                                    <g:else>${listFieldPropertyValue}</g:else>
                                                </td>
                                            </g:if>
                                            <g:else>
                                                <td align="center"/>
                                            </g:else>
                                        </g:each>
                                    </tr>
                                </g:each>
                                </tbody>
                            </table>
                        </div>
                    </g:if>
                    <g:elseif test="${column?.field instanceof StaticField && column?.field?.header}">
                        <div class="box-cards-title">
                            <span style="${column?.field?.style}">${column?.field.getValue(user?.id)}</span>
                        </div>
                    </g:elseif>
                    <g:else>
                        <div class="element2">
                            <g:set var="fieldValue" value="${column?.field.getValue(user?.id)}"/>
                            <g:if test="${column?.field instanceof StaticField}">
                                <g:if test="${column?.field?.label}">
                                    <label><span style="${column?.field?.style}">${column.field.label}:</span></label>
                                </g:if>
                            </g:if>
                            <g:else>
                                <g:if test="${column?.field?.label || column?.field?.name}">
                                    <label><span>${column.field.label ?: column.field.name}:</span></label>
                                </g:if>
                            </g:else>
                            <span class="value" style="${column?.field?.style}">
                                <g:if test="${fieldValue}">
                                    <g:if test="${column?.field.isMoneyProperty(listFieldProperty)}">
                                        $
                                    </g:if>
                                    <g:if test="${NumberUtils.isNumber(fieldValue.toString())}">
                                        <g:formatNumber number="${fieldValue}" maxFractionDigits="2"/>
                                    </g:if>
                                    <g:else>${fieldValue}</g:else>
                                </g:if>
                            </span>
                        </div>
                    </g:else>
                </g:if>
            </g:each>
        </div>
    </g:each>
</div>