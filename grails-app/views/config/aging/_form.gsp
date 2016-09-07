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

<%@page import="com.sapienter.jbilling.server.process.db.AgeingEntityStepDTO" %>

<div class="form-edit">

    <div class="heading">
        <strong><g:message code="configuration.title.collections"/></strong>
    </div>

    <g:form name="save-aging-form" action="saveAging" useToken="true">
        <g:render template="/config/aging/steps" model="[ageingSteps:ageingSteps]"/>
    </g:form>
    <g:settingEnabled property="collections.run.ui">
        <g:form name="run-collections-form" action="runCollectionsForDate" useToken="true">
            <g:render template="/config/aging/run"/>
        </g:form>
    </g:settingEnabled>
</div>
