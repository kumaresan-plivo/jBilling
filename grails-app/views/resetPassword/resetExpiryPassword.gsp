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
<%@ page import="com.sapienter.jbilling.common.Util; com.sapienter.jbilling.server.user.db.CompanyDTO" %>

<head>
    <meta name="layout" content="public"/>

    <title><g:message code="login.page.title"/></title>

    <r:script disposition="head">
        var RecaptchaOptions = {
            theme:'white'
        };

        $(document).ready(function () {
            $('#reset_password input[name="username"]').focus();

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
        <g:form controller="resetPassword" action="resetPassword" useToken="true">
            <fieldset>

                <div class="form-columns">
                    <g:applyLayout name="form/select">
                        <g:set var="forwordEntityName" value="${CompanyDTO.get(forwordEntityId)?.description}"/>
                        <content tag="label"><g:message code="login.prompt.client.id"/></content>
                        <content tag="label.for">company</content>
                        <span>${forwordEntityName}</span>
                     </g:applyLayout>
					<g:hiddenField name = "company" value="${forwordEntityId}"/>	
                    <g:applyLayout name="form/input">
                        <content tag="label"><g:message code="forgotPassword.prompt.userName"/></content>
                        <content tag="label.for">username</content>
                        <g:textField class="field" name="username" value=""/>
                    </g:applyLayout>

                    <g:applyLayout name="form/input">
                        <content tag="label"><g:message code="forgotPassword.prompt.oldPassword"/></content>
                        <content tag="label.for">oldPassword</content>
                        <g:passwordField class="field" name="oldPassword"  value=""/>
                    </g:applyLayout>

                    <g:applyLayout name="form/input">
                        <content tag="label"><g:message code="forgotPassword.prompt.newPassword"/></content>
                        <content tag="label.for">newPassword</content>
                        <g:passwordField class="field" id="newPassword" name="newPassword" value=""/>
                    </g:applyLayout>

                    <g:applyLayout name="form/input">
                        <content tag="label"><g:message code="forgotPassword.prompt.confirmPassword"/></content>
                        <content tag="label.for">confirmPassword</content>
                        <g:passwordField class="field" id="confirmPassword" name="confirmPassword" value=""/>
                    </g:applyLayout>

                    <script type="text/javascript"
                            src="https://www.google.com/recaptcha/api/challenge?k=${Util.getSysProp('recaptcha.public.key')}">
                    </script>
                    <noscript>
                        <iframe src="https://www.google.com/recaptcha/api/noscript?k=${Util.getSysProp('recaptcha.public.key')}"
                                height="300" width="500" frameborder="0"></iframe><br>
                        <textarea name="recaptcha_challenge_field" rows="3" cols="40">
                        </textarea>
                        <input type="hidden" name="recaptcha_response_field"
                               value="manual_challenge">
                    </noscript>

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
