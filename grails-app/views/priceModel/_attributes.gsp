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

<%@ page import="org.apache.commons.lang.StringUtils;" %>
<%@ page import="org.hibernate.criterion.CriteriaSpecification;" %>
<%@ page import="com.sapienter.jbilling.server.user.db.CompanyDAS;" %>
<%@ page import="com.sapienter.jbilling.server.pricing.cache.MatchType;" %>
<%@ page import=" com.sapienter.jbilling.server.user.db.CompanyDTO;" %>
<%@ page import="com.sapienter.jbilling.server.pricing.db.RateCardDTO;" %>
<%@ page import="com.sapienter.jbilling.server.item.db.ItemDTO;" %>
<%@ page import="com.sapienter.jbilling.server.pricing.db.PriceModelStrategy;" %>
<%@ page import="com.sapienter.jbilling.server.pricing.db.RouteRateCardDTO;" %>

<%--
  Editor form for price model attributes.

  This template is not the same as the attribute UI in the plan builder. The plan builder
  uses remote AJAX calls that can only be used in a web-flow. This template is to be used
  for standard .gsp pages.

  @author Brian Cowdery
  @since  02-Feb-2011
--%>

<g:set var="attributeIndex" value="${0}"/>
<g:set var="attrs" value="${model?.attributes ? new LinkedHashMap<String, String>(model?.attributes) : new LinkedHashMap<String, String>()}"/>

<!-- all required attribute definitions -->
<g:each var="definition" in="${type?.strategy?.attributeDefinitions}">
    <g:set var="attributeIndex" value="${attributeIndex + 1}"/>

    <g:set var="attribute" value="${attrs?.remove(definition.name)}"/>

    <g:if test="${definition.name== 'pool_item_id'}">
        <g:hiddenField name="model.${modelIndex}.attribute.${attributeIndex}.name" value="${definition.name}"/>
        <g:applyLayout name="form/select">
            <content tag="label"><g:message code="price.strategy.${type?.name()}.${definition?.name}"/><g:if test="${definition?.required}"><span id="mandatory-meta-field">*</span></g:if></content>
            <content tag="label.for">model.${modelIndex}.attribute.${attributeIndex}.value</content>
            <content tag="label.class">toolTipElement</content>
            <content tag="label.title"><g:message code="price.strategy.${type?.name()}.${definition?.name}.tooltip.message"/></content>
             
            <g:select name="model.${modelIndex}.attribute.${attributeIndex}.value" from="${priceModelData}"
                      optionKey="id" optionValue="${{it.id + ' - ' + it.description}}" value="${attribute as Integer}"
                      class="toolTipElement" title="${message(code: 'price.strategy.' + type.name() + '.' + definition?.name + '.tooltip.message')}"/>
        </g:applyLayout>
    </g:if>
    <g:elseif test="${definition.name== 'typeId'&&(type==PriceModelStrategy.ITEM_SELECTOR||type==PriceModelStrategy.ITEM_PERCENTAGE_SELECTOR)}">
        <g:hiddenField name="model.${modelIndex}.attribute.${attributeIndex}.name" value="${definition.name}"/>
        <g:applyLayout name="form/select">
            <content tag="label"><g:message code="price.strategy.${type?.name()}.${definition?.name}"/><g:if test="${definition?.required}"><span id="mandatory-meta-field">*</span></g:if></content>
            <content tag="label.for">model.${modelIndex}.attribute.${attributeIndex}.value</content>
            <content tag="label.class">toolTipElement</content>
            <content tag="label.title"><g:message code="price.strategy.${type?.name()}.${definition?.name}.tooltip.message"/></content>
            <g:select name="model.${modelIndex}.attribute.${attributeIndex}.value"  from="${priceModelData}"
                      optionKey="id" optionValue="${{it.id + ' - ' + it.description}}" value="${attribute as Integer}"
                      class="toolTipElement" title="${message(code: 'price.strategy.' + type.name() + '.' + definition?.name + '.tooltip.message')}"/>
        </g:applyLayout>
    </g:elseif>

    <g:elseif test="${definition.name == 'rate_card_id'}">
        <g:hiddenField name="model.${modelIndex}.attribute.${attributeIndex}.name" value="${definition.name}"/>
        <g:applyLayout name="form/select">
            <%
                def list = RateCardDTO.createCriteria().list(){
                    createAlias("childCompanies","childCompanies", CriteriaSpecification.LEFT_JOIN)
                    if(product?.priceModelCompanyId == null) {
                        eq('global', true)
                    } else {
                        or {
                            eq('company.id', product?.priceModelCompanyId)
                            eq('childCompanies.id', product?.priceModelCompanyId)
                        }
                    }

                    order('id', 'desc')
                }
                def rateCards = list.unique()
            %>
            <content tag="label"><g:message code="price.strategy.${type?.name()}.${definition?.name}"/><g:if test="${definition?.required}"><span id="mandatory-meta-field">*</span></g:if></content>
            <content tag="label.for">model.${modelIndex}.attribute.${attributeIndex}.value</content>
            <content tag="label.class">toolTipElement</content>
            <content tag="label.title"><g:message code="price.strategy.${type?.name()}.${definition?.name}.tooltip.message"/></content>
            <g:select name="model.${modelIndex}.attribute.${attributeIndex}.value"
                      from="${rateCards}"
                      optionKey="id" optionValue="${{it.name}}" noSelection="['': StringUtils.EMPTY]"
                      value="${StringUtils.isNotEmpty(attribute)? attribute as Integer: attribute}"
                      class="toolTipElement"
                      title="${message(code: 'price.strategy.' + type?.name() + '.' + definition?.name + '.tooltip.message')}"/>
        </g:applyLayout>
    </g:elseif>

    <g:elseif test="${definition.name == 'route_rate_card_id'}">
        <%
            def company = CompanyDTO.get(session['company_id'])
            def companies = [company]
            if(company.getParent()) companies += company.getParent()
        %>
        <g:hiddenField name="model.${modelIndex}.attribute.${attributeIndex}.name" value="${definition.name}"/>
        <g:applyLayout name="form/select">
            <content tag="label"><g:message code="price.strategy.${type?.name()}.${definition?.name}"/><g:if test="${definition?.required}"><span id="mandatory-meta-field">*</span></g:if></content>
            <content tag="label.for">model.${modelIndex}.attribute.${attributeIndex}.value</content>
            <content tag="label.class">toolTipElement</content>
            <content tag="label.title"><g:message code="price.strategy.${type?.name()}.${definition?.name}.tooltip.message"/></content>
            <g:select name="model.${modelIndex}.attribute.${attributeIndex}.value"
                      from="${RouteRateCardDTO.findAllByCompanyInList(companies, [sort: 'id'])}"
                      optionKey="id" optionValue="${{it.name}}" value="${StringUtils.isNotEmpty(attribute)? attribute as Integer: attribute}" 
                      noSelection="['': StringUtils.EMPTY]"
                      
                      class="toolTipElement"
                      title="${message(code: 'price.strategy.' + type?.name() + '.' + definition?.name + '.tooltip.message')}"/>
        </g:applyLayout>
    </g:elseif>

    <g:elseif test="${definition.name == 'match_type'}">
        <g:hiddenField name="model.${modelIndex}.attribute.${attributeIndex}.name" value="${definition.name}"/>
        <g:applyLayout name="form/select">
            <content tag="label"><g:message code="price.strategy.${type?.name()}.${definition?.name}"/><g:if test="${definition?.required}"><span id="mandatory-meta-field">*</span></g:if></content>
            <content tag="label.for">model.${modelIndex}.attribute.${attributeIndex}.value</content>
            <content tag="label.class">toolTipElement</content>
            <content tag="label.title"><g:message code="price.strategy.${type?.name()}.${definition?.name}.tooltip.message"/></content>
            <g:select name="model.${modelIndex}.attribute.${attributeIndex}.value"
                      optionKey="${{it.name()}}" optionValue="${{message(code: 'price.strategy.' + type?.name() + '.' + definition?.name + '.' + it?.name())}}"
                      from="${MatchType.values()}" value="${attribute}"
                      class="toolTipElement"
                      title="${message(code: 'price.strategy.' + type?.name() + '.' + definition?.name + '.tooltip.message')}"/>
        </g:applyLayout>
    </g:elseif>

    <g:elseif test="${definition.name == 'duration_field_name'}">
        <g:applyLayout name="form/input">
            <content tag="label"><g:message code="price.strategy.${type?.name()}.${definition?.name}"/><g:if test="${definition?.required}"><span id="mandatory-meta-field">*</span></g:if></content>
            <content tag="label.for">model.${modelIndex}.attribute.${attributeIndex}.value</content>
            <content tag="label.class">toolTipElement</content>
            <content tag="label.title"><g:message code="price.strategy.${type?.name()}.${definition?.name}.tooltip.message"/></content>

            <g:hiddenField name="model.${modelIndex}.attribute.${attributeIndex}.name" value="${definition.name}"/>
            <g:textField class="field toolTipElement" name="model.${modelIndex}.attribute.${attributeIndex}.value"
                         value="${attribute}"
                         title="${message(code: 'price.strategy.' + type?.name() + '.' + definition?.name + '.tooltip.message')}"/>
        </g:applyLayout>
    </g:elseif>

    <g:else>
        <g:applyLayout name="form/input">
            <content tag="label"><g:message code="price.strategy.${type?.name()}.${definition?.name}"/><g:if test="${definition?.required}"><span id="mandatory-meta-field">*</span></g:if></content>
            <content tag="label.for">model.${modelIndex}.attribute.${attributeIndex}.value</content>
            <content tag="label.class">toolTipElement</content>
            <content tag="label.title"><g:message code="price.strategy.${type?.name()}.${definition?.name}.tooltip.message"/></content>

            <g:hiddenField name="model.${modelIndex}.attribute.${attributeIndex}.name" value="${definition.name}"/>
            <g:textField class="field toolTipElement" name="model.${modelIndex}.attribute.${attributeIndex}.value"
                         value="${attribute}" title="${message(code: 'price.strategy.' + type?.name() + '.' + definition?.name + '.tooltip.message')}"/>
        </g:applyLayout>
    </g:else>
</g:each>
<g:if test="${type?.strategy?.usesDynamicAttributes()}">
    <!-- remaining user-defined attributes -->
    <g:set var="attrsEntrySet" value="${attrs?.entrySet()}"/>
    <g:set var="typeName" value="${type?.name()}"/>

    <g:if test="${attrsEntrySet}">
    %{-- All user defined attributes --}%
        <g:each var="attribute" in="${attrsEntrySet}" status="i">
            <g:set var="attributeIndex" value="${attributeIndex + 1}"/>
            <g:applyLayout name="form/attribute">
                <g:if test="${i == 0}">
                    <content tag="header.class">toolTipElement</content>
                    <content tag="header.name.title"><g:message code="price.strategy.${typeName}.attribute.name.tooltip.message"/></content>
                    <content tag="header.name"><g:message code="price.strategy.${typeName}.attribute.name.header"/></content>
                    <content tag="header.value.title"><g:message code="price.strategy.${typeName}.attribute.value.tooltip.message"/></content>
                    <content tag="header.value"><g:message code="price.strategy.${typeName}.attribute.value.header"/></content>
                </g:if>
                <content tag="name">
                    <g:textField class="field toolTipElement"
                                 title="${message(code: 'price.strategy.' + typeName + '.attribute.name.tooltip.message')}"
                                 name="model.${modelIndex}.attribute.${attributeIndex}.name"
                                 value="${attribute.key}"/>
                </content>
                <content tag="value">
                    <div class="inp-bg inp-desc">
                        <g:textField class="field toolTipElement"
                                     title="${message(code: 'price.strategy.' + typeName + '.attribute.value.tooltip.message')}"
                                     name="model.${modelIndex}.attribute.${attributeIndex}.value"
                                     value="${attribute.value}"/>

                        <a onclick="removeModelAttribute(this, ${modelIndex}, ${attributeIndex})">
                            <img src="${resource(dir: 'images', file: 'cross.png')}" alt="remove" class="toolTipElement" title="${message(code: 'price.strategy.COMMON.attributes.remove.tooltip.message')}"/>
                        </a>
                    </div>
                </content>

                <g:if test="${i == attrsEntrySet.size() - 1}">
                    <a onclick="addModelAttribute(this, ${modelIndex}, ${attributeIndex})">
                        <img src="${resource(dir:'images', file:'add.png')}" alt="add" class="toolTipElement" title="${message(code: 'price.strategy.COMMON.attributes.add.tooltip.message')}"/>
                    </a>
                </g:if>

            </g:applyLayout>
        </g:each>
    </g:if>
    <g:else>
        <!-- one empty row -->
        <g:set var="attributeIndex" value="${attributeIndex + 1}"/>
        <g:applyLayout name="form/attribute">
            <content tag="header.class">toolTipElement</content>
            <content tag="header.name.title"><g:message code="price.strategy.${typeName}.attribute.name.tooltip.message"/></content>
            <content tag="header.name"><g:message code="price.strategy.${typeName}.attribute.name.header"/></content>
            <content tag="header.value.title"><g:message code="price.strategy.${typeName}.attribute.value.tooltip.message"/></content>
            <content tag="header.value"><g:message code="price.strategy.${typeName}.attribute.value.header"/></content>

            <content tag="name">
                <g:textField class="field toolTipElement" title="${message(code: 'price.strategy.' + typeName + '.attribute.name.tooltip.message')}" name="model.${modelIndex}.attribute.${attributeIndex}.name"/>
            </content>
            <content tag="value">
                <div class="inp-bg">
                    <g:textField class="field toolTipElement" title="${message(code: 'price.strategy.' + typeName + '.attribute.value.tooltip.message')}" name="model.${modelIndex}.attribute.${attributeIndex}.value"/>
                </div>
            </content>

            <a onclick="addModelAttribute(this, ${modelIndex}, ${attributeIndex})">
                <img src="${resource(dir:'images', file:'add.png')}" alt="add" class="toolTipElement" title="${message(code: 'price.strategy.COMMON.attributes.add.tooltip.message')}"/>
            </a>
        </g:applyLayout>
    </g:else>
</g:if>
