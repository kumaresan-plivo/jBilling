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

<%@ page import="com.sapienter.jbilling.server.user.db.CompanyDTO" %>

<head>
    <meta name="layout" content="public" />

    <title><g:message code="login.page.title"/></title>
    <r:require modules="jquery-validate"/>

    <r:script disposition="head">
        $(document).ready(function() {
            $('#login input[name="j_username"]').focus();
            $(document).keypress(function(e) {
                if(e.which == 13) {
                    $(this).blur();
                    $('#submitLink').focus().click();
                }
            });

            $("#login-form").validate({
                // Enable validation on specific hidden field. By default hidden fields are not validated.
                ignore: "not:hidden('#j_client_id')",
                // Validation rules
                rules: {
                    j_username: {
                        required: true
                    },
                    j_password: {
                        required: true
                    },
                    j_client_id: {
                        required: true,
                        digits: true
                    }
                },
                // Validation messages
                messages: {
                    j_client_id: {
                        digits: '${message(code: 'login.field.number.error')}'
                    }
                },
                // Handles all invalid submit actions
                invalidHandler: function(event, validator){
                    if (validator.errorList.length > 0) {
                        $('#loginError').find('span').text(validator.errorList[0].message);
                        validator.errorList[0].element.focus();
                    }
                },
                // Used to remove default error labels
                errorPlacement: function(error, element){}
            });
            // Override the default required message
            jQuery.extend(jQuery.validator.messages, {
                required: '${message(code: 'login.field.required.error')}'
            });
        });
    </r:script>
</head>
<body>

    <g:render template="/layouts/includes/messages"/>

    <div id="login" class="form-edit">
        <div class="heading">
            <strong><g:message code="login.prompt.title"/></strong>
        </div>
        <div class="form-hold">
            <form action='${postUrl}' method='POST' id='login-form' autocomplete='off'>
                
                <g:hiddenField name="interactive_login" value="true"/>                

                <fieldset>

                    <div class="form-columns">
                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="login.prompt.username"/></content>
                            <content tag="label.for">username</content>
                            <g:textField class="field" name="j_username" value="${params.userName}"/>
                        </g:applyLayout>

                        <g:applyLayout name="form/input">
                            <content tag="label"><g:message code="login.prompt.password"/></content>
                            <content tag="label.for">password</content>
                            <g:passwordField class="field" name="j_password"/>
                        </g:applyLayout>

                        <g:if test="${companyId}">
                            <div class="center-align">
                                <g:hiddenField name="j_client_id" value="${companyId}"/>
                            </div>
                        </g:if>
                        <g:else>
                            <g:applyLayout name="form/input">
                                <content tag="label"><g:message code="login.prompt.client.id"/></content>
                                <content tag="label.for">j_client_id</content>
                                <g:textField class="field" name="j_client_id" />
                            </g:applyLayout>
                        </g:else>

                        <div id="loginError" class="center-align">
                            <span id="errorSpan" class="narrow error"></span>
                        </div>

                        <g:applyLayout name="form/checkbox">
                            <content tag="label"><g:message code="login.prompt.remember.me"/></content>
                            <content tag="label.for">${rememberMeParameter}</content>
                            <g:checkBox class="cb checkbox" name="${rememberMeParameter}" checked="${hasCookie}"/>
                        </g:applyLayout>

                        <g:applyLayout name="form/text">
                            <content tag="label">&nbsp;</content>
                            <g:link controller="resetPassword"><g:message code="login.prompt.forgotPassword" /></g:link>
                        </g:applyLayout>

                        <br/>
                    </div>

                    <div class="buttons">
                        <ul>
                            <li>
                                <a href="#" id="submitLink" class="submit save" onclick="$('#login-form').submit();">
                                    <span><g:message code="login.button.submit"/></span>
                                </a>
                            </li>
                        </ul>
                    </div>
                </fieldset>
            </form>
        </div>
    </div>

</body>
</html>