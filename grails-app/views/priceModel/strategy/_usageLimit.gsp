<%@ page import="com.sapienter.jbilling.server.user.db.CompanyDTO" %>

<%--
  	Usage Limit
--%>

<g:set var="planIndex" value="${planIndex ?: ''}"/>
<g:set var="defaultCurrency" value="${CompanyDTO.get(session['company_id']).getCurrency()}"/>
<g:hiddenField name="model.${modelIndex}.id" value="${model?.id}"/>

<g:applyLayout name="form/select">
    <content tag="label"><g:message code="plan.model.type"/></content>
    <content tag="label.for">model.${modelIndex}.type</content>
    <content tag="label.title"><g:message code="price.strategy.COMMON.pricing.tooltip.message"/></content>
    <content tag="label.class">toolTipElement</content>
    <g:select name="model.${modelIndex}.type" class="model-type toolTipElement"
              title="${message(code: 'price.strategy.COMMON.pricing.tooltip.message')}"
              from="${types}"
              keys="${types*.name()}"
              valueMessagePrefix="price.strategy"
              value="${model?.type ?: type.name()}"/>

    <g:hiddenField name="model.${modelIndex}.oldType" value="${model?.type ?: type.name()}"/>

    <a onclick="openHelpDialog(${planIndex + type?.name() + modelIndex});">
        <img class="toolTipElement" title="${message(code: 'price.strategy.COMMON.pricing.help.tooltip.message')}" src="${resource(dir: 'images', file: 'question.png')}" alt="more">
    </a>
</g:applyLayout>

<g:hiddenField name="model.${modelIndex}.rateAsDecimal" value="${BigDecimal.ZERO}"/>

<g:applyLayout name="form/select">
    <content tag="label"><g:message code="prompt.user.currency"/></content>
    <content tag="label.for">model.${modelIndex}.currencyId</content>
    <content tag="label.title"><g:message code="price.strategy.COMMON.currency.tooltip.message"/></content>
    <content tag="label.class">toolTipElement</content>
    <g:select name="model.${modelIndex}.currencyId"
              class="toolTipElement"
              title="${message(code: 'price.strategy.COMMON.currency.tooltip.message')}"
              from="${currencies}"
              optionKey="id" optionValue="${{it.getDescription(session['language_id'])}}"
              value="${model?.currencyId ?: defaultCurrency?.id}" />
</g:applyLayout>