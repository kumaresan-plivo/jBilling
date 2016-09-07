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
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>

<%@ page import="com.sapienter.jbilling.common.Util; com.sapienter.jbilling.server.user.db.CompanyDTO" %>

<head>
    <meta name="layout" content="public"/>

    <title><g:message code="login.page.title"/></title>

    <r:script disposition="head">
        var RecaptchaOptions = {
            theme:'white'
        };

        $(document).ready(function () {
            $('#reset_password input[name="email"]').focus();

            $(document).keypress(function (e) {
                if (e.which == 13) {

                    $(this).blur();
                    $('#reset_password form').submit();
                }
            });
        });

    </r:script>

    <style type="text/css">
    #recaptcha_widget_div label {
        float: none;
    }

    #recaptcha_widget_div a img {
        top: 0px;
        left: 0px;
    }

    #recaptcha_widget_div span {
        font-weight: normal;
        line-height: 0 !important;
    }

    #recaptcha_widget_div {
        margin-left: 85px;
        margin-top: 12px;
    }
    </style>
</head>

<body>

<g:render template="/layouts/includes/messages"/>

<div id="reset_password" class="form-edit">
    <div class="heading">
        <strong><g:message code="forgotPassword.prompt.title"/></strong>
    </div>

    <div class="form-hold">
        <g:form controller="resetPassword" action="captcha" useToken="true">
            <fieldset>

                <div class="form-columns">

                    <g:if test="${useEmail}">
                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="forgotPassword.prompt.email"/></content>
                            <content tag="label.for">email</content>
                            <g:textField class="field" name="email" value=""/>
                        </g:applyLayout>
                    </g:if>
                    <g:else>
                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="forgotPassword.prompt.userName"/></content>
                            <content tag="label.for">userName</content>
                            <g:textField class="field" name="userName" value=""/>
                        </g:applyLayout>
                    </g:else>
                    <g:if test="${params.companyId}">
                        <div class="center-align">
                            <g:hiddenField name="companyId" value="${params.companyId}"/>
                        </div>
                    </g:if>
                    <g:else>
                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="login.prompt.client.id"/></content>
                            <content tag="label.for">userName</content>
                            <g:textField class="field" name="companyId" value=""/>
                        </g:applyLayout>
                    </g:else>

                    <g:if test="${captchaEnabled}">
                        <script type="text/javascript"
                                src="https://www.google.com/recaptcha/api/challenge?k=${publicKey}">
                        </script>
                        <noscript>
                            <iframe src="https://www.google.com/recaptcha/api/noscript?k=${publicKey}"
                                    height="300" width="500" frameborder="0"></iframe><br>
                            <textarea name="recaptcha_challenge_field" rows="3" cols="40">
                            </textarea>
                            <input type="hidden" name="recaptcha_response_field"
                                   value="manual_challenge">
                        </noscript>
                    </g:if>

                    <br/>
                </div>

                <div class="buttons">
                    <ul>
                        <li>
                            <a href="#" class="submit save" onclick="$('#reset_password form').submit();">
                                <span><g:message code="forgotPassword.button.submit"/></span>
                            </a>
                        </li>
                    </ul>
                </div>
            </fieldset>
        </g:form>
    </div>
</div>

</body>
</html>