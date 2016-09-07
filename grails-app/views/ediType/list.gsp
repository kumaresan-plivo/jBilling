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

<%@ page import="jbilling.EdiTypeController; com.sapienter.jbilling.common.Constants" %>

<html>
<head>
    <meta name="layout" content="panels" />
    <g:preferenceEquals preferenceId="${Constants.PREFERENCE_USE_JQGRID}" value="1">
        <link type="text/css" href="${resource(file: '/css/ui.jqgrid.css')}" rel="stylesheet" media="screen, projection" />
        <g:javascript src="jquery.jqGrid.min.js"  />
        <g:javascript src="jqGrid/i18n/grid.locale-${session.locale.language}.js"  />
    </g:preferenceEquals>
</head>
<body>
    <!-- selected configuration menu item -->
    <content tag="menu.item">mediation</content>

<content tag="column1">

        <g:render template="ediTypesTemplate" model="['ediTypes': ediTypes, selected:selected]"/>
</content>

    <content tag="column2">
        <g:if test="${selected || ediTypeWS}">
            <g:if test="${selected}">
                <g:render template="show" model="[ediType : selected]"/>
            </g:if>
            <g:if test="${ediTypeWS}">
                <g:render template="edit" model="[ediType:ediTypeWS,companies:new jbilling.EdiTypeController().retrieveCompanies()]" />
            </g:if>
        </g:if>
        <g:else>
            <div class="heading"><strong><em><g:message code="edi.type.not.selected"/></em></strong></div>
            <div class="box"><div class="sub-box"><em><g:message code="edi.type.not.selected.message"/></em></div></div>
            <div class="btn-box"></div>
        </g:else>
    </content>
</body>
</html>
