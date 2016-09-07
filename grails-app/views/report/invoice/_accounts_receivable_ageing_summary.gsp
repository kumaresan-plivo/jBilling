<%@ page import="com.sapienter.jbilling.server.util.EnumerationBL" %>
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

<%-- Parameters for the Accounts Receivable Ageing Summary report --%>
<div class="form-columns">
    <g:applyLayout name="form/select">
        <content tag="label"><g:message code="report.ageing.balance.division.label"/></content>
        <content tag="label.for">division</content>
        <g:select name="division" optionKey="value" from="${new EnumerationBL().getEnumerationByName('DIVISION', session['company_id'] as Integer)?.values}" valueMessagePrefix="customer.division" noSelection="['':'']"/>
    </g:applyLayout>

    <g:applyLayout name="form/select">
        <content tag="label"><g:message code="report.ageing.balance.customerStatuses.label"/></content>
        <content tag="label.for">customer_statuses</content>
        <g:select name="customer_statuses" multiple="true" optionKey="value" optionValue="value" from="${new EnumerationBL().getEnumerationByName('Termination', session['company_id'] as Integer)?.values}" noSelection="['Active':'Active']"/>
    </g:applyLayout>
</div>