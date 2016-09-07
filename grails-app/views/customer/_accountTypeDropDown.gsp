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

<g:applyLayout name="form/select">
	<content tag="label"><g:message code="customer.detail.account.type"/></content>
   	<content tag="label.for">user.accountTypeId</content>
   	<g:select name="accountTypeId" from="${accountTypes}"
    			optionKey="id" optionValue="description"/>
</g:applyLayout>