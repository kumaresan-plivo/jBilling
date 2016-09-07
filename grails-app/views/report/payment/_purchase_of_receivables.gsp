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

<%@ page import="org.joda.time.DateTime; com.sapienter.jbilling.server.util.EnumerationBL; com.sapienter.jbilling.server.util.db.EnumerationValueDTO; com.sapienter.jbilling.server.item.ItemTypeBL; com.sapienter.jbilling.server.item.ItemTypeWS; com.sapienter.jbilling.server.item.ItemBL; com.sapienter.jbilling.server.item.db.ItemDTO; com.sapienter.jbilling.server.user.db.CompanyDTO; com.sapienter.jbilling.server.user.db.CompanyDAS" %>

<%--
  Summary Billing Register

  @author Gerhard Maree
  @since  21-Sep-2015
--%>

<div class="form-columns">

    <g:applyLayout name="form/date">
        <content tag="label"><g:message code="start_date"/></content>
        <content tag="label.for">start_date</content>
        <g:textField class="field" name="start_date" value="${formatDate(date: new DateTime().withTimeAtStartOfDay().withDayOfMonth(1).toDate(), formatName: 'datepicker.format')}" onblur="validateDate(this)"/>
    </g:applyLayout>

    <g:applyLayout name="form/date">
        <content tag="label"><g:message code="end_date"/></content>
        <content tag="label.for">end_date</content>
        <g:textField class="field" name="end_date" value="${formatDate(date: new DateTime().dayOfMonth().withMaximumValue().toDate(), formatName: 'datepicker.format')}" onblur="validateDate(this)"/>
    </g:applyLayout>

    <g:applyLayout name="form/select">
        <content tag="label"><g:message code="report.billing.register.label.division"/></content>
        <content tag="label.for">division</content>
        <g:select name="division" from="${new EnumerationBL().getEnumerationByName('DIVISION', session['company_id'])?.values}" optionKey="value" optionValue="value" noSelection="${['':'All']}" />
    </g:applyLayout>

    <input id="entityNames" type="hidden" name="entityNames" value="" />
</div>

<script type="text/javascript">
    $(function() {
        $('#childs').change(function() {
            var val = '';
            $('#childs option:selected').each( function() {
                if(val != '') val += ', ';
                val += $(this).text();
            });
            $('#entityNames').val(val);
        });

        $("label[for='childs']").text('<g:message code="report.billing.register.label.company"/>');
    });
</script>