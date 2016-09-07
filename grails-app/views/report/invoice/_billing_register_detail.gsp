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

<%@ page import="com.sapienter.jbilling.server.util.EnumerationBL; com.sapienter.jbilling.server.util.db.EnumerationValueDTO; com.sapienter.jbilling.server.item.ItemTypeBL; com.sapienter.jbilling.server.item.ItemTypeWS; com.sapienter.jbilling.server.item.ItemBL; com.sapienter.jbilling.server.item.db.ItemDTO; com.sapienter.jbilling.server.user.db.CompanyDTO; com.sapienter.jbilling.server.user.db.CompanyDAS; org.joda.time.DateMidnight" %>

<%--
  Summary Billing Register

  @author Gerhard Maree
  @since  21-Sep-2015
--%>

<div class="form-columns">

    %{-- Negation of test to display child entities in _show.gsp --}%
    <input type="hidden" name="entity_id" value="${!(childEntities?.size() > 0 && company?.parent == null) ? session['company_id'] : 0}" />

    <g:applyLayout name="form/date">
        <content tag="label"><g:message code="start_date"/></content>
        <content tag="label.for">start_date</content>
        <g:textField class="field" name="start_date" value="${formatDate(date: new DateMidnight().minusMonths(1).toDate(), formatName: 'datepicker.format')}" onblur="validateDate(this)"/>
    </g:applyLayout>

    <g:applyLayout name="form/date">
        <content tag="label"><g:message code="end_date"/></content>
        <content tag="label.for">end_date</content>
        <g:textField class="field" name="end_date" value="${formatDate(date: new Date(), formatName: 'datepicker.format')}" onblur="validateDate(this)"/>
    </g:applyLayout>

    <g:applyLayout name="form/select">
        <content tag="label"><g:message code="report.billing.register.label.itemType"/></content>
        <content tag="label.for">itemType</content>
        <g:select id="itemType" name="item_type_id" from="${new ItemTypeBL().getItemCategoriesByEntity(session['company_id'])}" optionKey="id" optionValue="description" noSelection="${['null':'All']}" />
    </g:applyLayout>
    <input id="itemTypeName" type="hidden" name="itemTypeName" value="" />

    <g:applyLayout name="form/select">
        <content tag="label"><g:message code="report.billing.register.label.item"/></content>
        <content tag="label.for">item</content>
        <g:select id="item" name="item_id" from="${new ItemBL().getAllItemsByEntity(session['company_id'])}" optionKey="id" optionValue="description" noSelection="${['null':'All']}" />
    </g:applyLayout>
    <input id="itemName" type="hidden" name="itemName" value="" />

    <g:applyLayout name="form/select">
        <content tag="label"><g:message code="report.billing.register.label.state"/></content>
        <content tag="label.for">state</content>
        <g:select name="state" from="${new EnumerationBL().getEnumerationByName('STATE', session['company_id'])?.values}" optionKey="value" optionValue="value" noSelection="${['':'All']}" />
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
        $('#itemType').change(function() {
            $('#itemTypeName').val($('#itemType option:selected').text());
        });

        $('#item').change(function() {
            $('#itemName').val($('#item option:selected').text());
        });

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