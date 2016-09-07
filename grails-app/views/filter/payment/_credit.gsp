<%@ page import="com.sapienter.jbilling.server.payment.db.PaymentMethodDAS; com.sapienter.jbilling.client.util.Constants" %>
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

<div id="${filter.name}">
    <span class="title <g:if test='${filter.value}'>active</g:if>"><g:message code="filters.credit.title"/></span>
    <g:remoteLink class="delete" controller="filter" action="remove" params="[name: filter.name]" update="filters"/>

    <div class="slide">
        <fieldset>
            <div class="input-row">
                <div class="select-filter">
                    <g:select name="filters.${filter.name}.stringValue"
                              from="${[[value:Constants.PAYMENT_METHOD_CREDIT,label:message(code:'prompt.yes')],[value:new PaymentMethodDAS().findAllValidMethods().collect {it.id}.join(','),label:message(code:'prompt.no')]]}"
                              optionKey="value"
                              optionValue="label"
                              value="${filter.stringValue}"/>
                </div>
                <label for="filters.${filter.name}.integerValue"><g:message code="filters.credit.label"/></label>
            </div>
        </fieldset>
    </div>
</div>
