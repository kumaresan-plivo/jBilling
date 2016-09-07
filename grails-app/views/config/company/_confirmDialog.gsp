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

<div id="confirm-dialog" class="bg-lightbox" title="<g:message code="popup.confirm.title"/>" style="display:none;">
    <g:form name="confirm-command-form" url="[controller: 'signup', action: 'copyCompany', id: id, isCompanyChild:isCompanyChild, copyProducts:copyProducts, copyPlans:copyPlans]">
        <g:hiddenField name="id" value="${id}"/>
        <!-- confirm dialog content body -->
        <table style="margin: 3px 0 0 10px">
            <tbody><tr>
                <td valign="top">
                    <img src="${resource(dir:'images', file:'icon34.gif')}" alt="confirm">
                </td>
                <td class="col2" style="padding-left: 7px">

                    <p id="confirm-dialog-msg">
                        <g:if test="${message}">
                            <g:message code="${message}" args="[id]"/>
                        </g:if>
                    </p>

                </td>
            </tr></tbody>
        </table>
        <table style="margin: 3px 0 0 10px">
            <tbody>
            <tr>
                <td class="col1" style="padding-left: 7px">
                    <p id="confirm-dialog-checkbox-products">
                        <g:applyLayout name="form/checkbox">
                            <content tag="label"><g:message code="copy.company.confirm.products" args="[id]"/></content>
                            <content tag="label.for">childCompanyFlag</content>
                            <g:checkBox class="cb checkbox" name="copyProducts"/>
                        </g:applyLayout>
                    </p>

                    <p id="confirm-dialog-checkbox-plans">
                        <g:applyLayout name="form/checkbox">
                            <content tag="label"><g:message code="copy.company.confirm.plans" args="[id]"/></content>
                            <content tag="label.for">childCompanyFlag</content>
                            <g:checkBox class="cb checkbox" name="copyPlans"/>
                        </g:applyLayout>
                    </p>

                    <p id="confirm-dialog-checkbox">
                        <g:applyLayout name="form/checkbox">
                            <content tag="label"><g:message code="copy.company.confirm.child" args="[id]"/></content>
                            <content tag="label.for">childCompanyFlag</content>
                            <g:checkBox class="cb checkbox" name="isCompanyChild"/>
                        </g:applyLayout>
                    <br/>
                    <div id="childCompanyDiv">
                        <g:applyLayout name="form/text">
                            <content tag="label"><g:message code="copy.company.child.template.label"/></content>
                            <g:textField name="childCompany" value=""/>
                        </g:applyLayout>
                    </div>

                    </p>

                </td>
            </tr></tbody>
        </table>
    </g:form>
</div>

<script type="text/javascript">
    $(function() {
        $("#childCompanyDiv").hide();
        setTimeout(function() {
            $('#confirm-dialog.ui-dialog-content').remove();
            $('#confirm-dialog').dialog({
                autoOpen: false,
                height: 300,
                width: 450,
                modal: true,
                buttons: {
                    '<g:message code="prompt.yes"/>': function() {
                        $("#confirm-command-form").submit();
                        $(this).dialog('close');
                    },
                    '<g:message code="prompt.no"/>': function() {
                        $(this).dialog('close');
                    }
                }
            });
        }, 100);

        $('#isCompanyChild').change(function() {
            if($(this).is(":checked")) {
                $("#childCompanyDiv").show();
            } else {
                $("#childCompanyDiv").val("");
                $("#childCompanyDiv").hide();
            }
        });
    });


    function show() {
        $('#confirm-dialog').dialog('open');
    }
</script>
