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

<%--
  Editor form for the ageing steps

  @author Panche Isajeski
  @since  24-Jan-2013
--%>

<%@page import="com.sapienter.jbilling.server.process.db.AgeingEntityStepDTO" %>

    <div id="ageing" class="form-hold">
        <g:hiddenField name="recCnt" value="${ageingSteps?.length}"/>
        <fieldset>
            <div class="form-columns single">
                <table id="ageingStepTable" class="innerTable">
                    <thead class="innerHeader">
                    <tr>
                        <th class="left tiny2"><g:message code="config.ageing.step.id"/></th>
                        <th class="left tiny2"><g:message code="config.ageing.step"/></th>
                        <th class="left tiny2"><g:message code="config.ageing.forDays"/></th>
                        <th class="left medium"><g:message code="config.ageing.sendNotification"/></th>
                        <th class="left medium"><g:message code="config.ageing.retryPayment"/></th>
                        <th class="left medium"><g:message code="config.ageing.suspended"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each status="iter" var="step" in="${ageingSteps}">
                        <tr>
                            <td class="tiny2">
                                <strong>${step.statusId}</strong>
                            </td>
                            <td class="medium2">
                                <g:textField class="inp-bg inp-desc" name="obj[${iter}].statusStr" value="${step.statusStr}"/>
                            </td>
                            <td class="medium">
                                <g:textField class="inp-bg numericOnly inp4" name="obj[${iter}].days" value="${step.days}"/>
                            </td>
                            <td class="tiny">
                                <g:checkBox class="cb checkbox" name="obj[${iter}].sendNotification" checked="${step.sendNotification }"/>

                            </td>
                            <td class="tiny">
                                <g:checkBox class="cb checkbox" name="obj[${iter}].paymentRetry" checked="${step.paymentRetry }"/>
                            </td>
                            <td class="tiny">
                                <g:checkBox class="cb checkbox" name="obj[${iter}].suspended" checked="${step.suspended }"/>
                                <g:hiddenField value="${step?.statusId}" name="obj[${iter}].statusId"/>
                                <g:hiddenField value="placeholder_text" name="obj[${iter}].welcomeMessage"/>
                                <g:hiddenField value="placeholder_text" name="obj[${iter}].failedLoginMessage"/>
                                <g:if test="${!step?.inUse}">
                                    <a onclick="removeAgeingStep(this, ${iter});">
                                        <img src="${resource(dir:'images', file:'cross.png')}" alt="remove"/>
                                    </a>
                                </g:if>
                            </td>
                        </tr>
                    </g:each>

                    <g:set var="newStepIndex" value="${ageingSteps.size()}"/>
                    <tr>
                        <td class="tiny2">
                            <strong></strong>
                        </td>
                        <td class="medium2">
                            <g:textField class="inp-bg inp-desc" name="obj[${newStepIndex}].statusStr" onchange="addAgeingStep(this, ${newStepIndex})"/>
                        </td>
                        <td class="medium">
                            <g:textField class="inp-bg numericOnly inp4" name="obj[${newStepIndex}].days"/>
                        </td>
                        <td class="tiny">
                            <g:checkBox class="cb checkbox" name="obj[${newStepIndex}].sendNotification" checked="${false}"/>

                        </td>
                        <td class="tiny">
                            <g:checkBox class="cb checkbox" name="obj[${newStepIndex}].paymentRetry" checked="${false}"/>
                        </td>
                        <td class="tiny">
                            <g:checkBox class="cb checkbox" name="obj[${newStepIndex}].suspended" checked="${false}"/>
                            <g:hiddenField name="obj[${newStepIndex}].statusId"/>
                            <g:hiddenField value="placeholder_text" name="obj[${newStepIndex}].welcomeMessage"/>
                            <g:hiddenField value="placeholder_text" name="obj[${newStepIndex}].failedLoginMessage"/>
                            <a onclick="addAgeingStep(this, ${newStepIndex})">
                                <img src="${resource(dir:'images', file:'add.png')}" alt="add"/>
                            </a>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </fieldset>
        <div class="btn-row">
            <a onclick="$('#save-aging-form').submit();" class="submit save"><span><g:message code="button.save"/></span></a>
            <g:link controller="config" action="index" class="submit cancel"><span><g:message code="button.cancel"/></span></g:link>
        </div>

        <g:hiddenField name="stepIndex"/>


        <script type="text/javascript">
            $(".numericOnly").keydown(function(event){
                // Allow only backspace, delete, left & right
                if ( event.keyCode==37 || event.keyCode== 39 || event.keyCode == 46 || event.keyCode == 8 || event.keyCode == 9 ) {
                    // let it happen, don't do anything
                }
                else {
                    // Ensure that it is a number and stop the keypress
                    if (event.keyCode < 48 || event.keyCode > 57 ) {
                        event.preventDefault();
                    }
                }
            });

            function addAgeingStep(element, stepIndex) {

                $('#stepIndex').val(stepIndex);

                $.ajax({
                    type: 'POST',
                    url: '${createLink(action: 'addAgeingStep')}',
                    data: $('#ageing').parents('form').serialize(),
                    success: function(data) {
                        $('#ageing').replaceWith(data);
                        $('input[name="obj['+stepIndex+'].days"]').focus();
                    }
                });
            }

            function removeAgeingStep(element, stepIndex) {

                $('#stepIndex').val(stepIndex);

                $.ajax({
                    type: 'POST',
                    url: '${createLink(action: 'removeAgeingStep')}',
                    data: $('#ageing').parents('form').serialize(),
                    success: function(data) {
                        $('#ageing').replaceWith(data);
                    }
                });
            }
        </script>
</div>