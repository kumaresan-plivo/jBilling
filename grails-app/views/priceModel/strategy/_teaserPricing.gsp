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

<%@ page import="com.sapienter.jbilling.server.pricing.strategy.TeaserPricingStrategy;" %>
<%@ page import="org.apache.commons.lang.StringUtils;" %>
<%@ page import="com.sapienter.jbilling.server.user.db.CompanyDTO;" %>
<%@ page import="com.sapienter.jbilling.server.pricing.db.PriceModelStrategy;" %>
<%@ page import="com.sapienter.jbilling.server.pricing.db.RouteRateCardDTO;" %>

<%--
  	Teaser Pricing
--%>

<g:set var="planIndex" value="${planIndex ?: ''}"/>
<g:set var="defaultCurrency" value="${CompanyDTO.get(session['company_id']).getCurrency()}"/>
<g:set var="attrs" value="${model?.attributes ? new LinkedHashMap<String, String>(model?.attributes) : new LinkedHashMap<String, String>()}"/>
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
        <img class="toolTipElement" title="${message(code: 'price.strategy.COMMON.pricing.help.tooltip.message')}"
             src="${resource(dir: 'images', file: 'question.png')}" alt="more">
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
              optionKey="id" optionValue="${{ it.getDescription(session['language_id']) }}"
              value="${model?.currencyId ?: defaultCurrency?.id}"/>
</g:applyLayout>


<%
    //If there is no price model we will create 2 cycles as template. The first will be flat and the
    //second a route rate card.
    TeaserPricingStrategy.CyclePrice templatePrice = new TeaserPricingStrategy.CyclePrice();
    templatePrice.fromCycle = Integer.valueOf(0);
    templatePrice.rate = "";
    templatePrice.strategy = TeaserPricingStrategy.Strategy.FLAT.name()

    SortedMap<Integer, TeaserPricingStrategy.CyclePrice> cyclePrices = TeaserPricingStrategy.buildCyclePriceInfo(attrs, false)
    if (cyclePrices.isEmpty()) {
        cyclePrices = new TreeMap<>();
        cyclePrices.put(templatePrice.fromCycle, templatePrice);

        TeaserPricingStrategy.CyclePrice price = new TeaserPricingStrategy.CyclePrice();
        price.fromCycle = Integer.valueOf(3);
        price.strategy = TeaserPricingStrategy.Strategy.RATE_CARD.name()
        cyclePrices.put(price.fromCycle, price);
    }

    def company = CompanyDTO.get(session['company_id'])
    def companies = [company]
    if (company.getParent()) companies += company.getParent()

    def rateCards = RouteRateCardDTO.findAllByCompanyInList(companies, [sort: 'id'])

%>

<g:set var="attributeIndex" value="${10}"/>

<g:each in="${cyclePrices.values()}" var="cyclePrice" status="cyclePriceIdx">
    <div id="cycle-price-${modelIndex}-${cyclePriceIdx}">
    %{-- From Cycle --}%
        <g:applyLayout name="form/input">
            <content tag="label"><g:message code="price.strategy.TEASER_PRICING.cycle"/></content>
            <content tag="label.for">model.${modelIndex}.attribute.${attributeIndex}.value</content>
            <content tag="label.class">toolTipElement</content>
            <content tag="label.title"><g:message code="price.strategy.TEASER_PRICING.cycle.tooltip.message"/></content>

            <g:hiddenField name="model.${modelIndex}.attribute.${attributeIndex}.name"
                           value="${TeaserPricingStrategy.PARAM_CYCLE_PREFIX + cyclePriceIdx}"/>
            <g:textField class="field toolTipElement" name="model.${modelIndex}.attribute.${attributeIndex++}.value"
                         value="${cyclePrice.fromCycle}"
                         title="${message(code: 'price.strategy.TEASER_PRICING.cycle.tooltip.message')}"/>

        </g:applyLayout>

    %{-- Pricing Strategy --}%
        <g:hiddenField name="model.${modelIndex}.attribute.${attributeIndex}.name"
                       value="${TeaserPricingStrategy.PARAM_PRICING_STRATEGY_PREFIX + cyclePriceIdx}"/>
        <g:applyLayout name="form/select">
            <content tag="label"><g:message code="price.strategy.TEASER_PRICING.strategy"/></content>
            <content tag="label.for">model.${modelIndex}.attribute.${attributeIndex}.value</content>
            <content tag="label.class">toolTipElement</content>
            <content tag="label.title"><g:message
                    code="price.strategy.TEASER_PRICING.strategy.tooltip.message"/></content>
            <g:select id="strategy-${modelIndex}-${cyclePriceIdx}" class="toolTipElement"
                      title="${message(code: 'price.strategy.TEASER_PRICING.strategy.tooltip.message')}"
                      from="${com.sapienter.jbilling.server.pricing.strategy.TeaserPricingStrategy.Strategy.values()}"
                      name="model.${modelIndex}.attribute.${attributeIndex++}.value"
                      valueMessagePrefix="price.strategy.TEASER_PRICING.strategy"
                      value="${TeaserPricingStrategy.Strategy.valueOf(cyclePrice.strategy)}"
                      onchange="displayCorrectRows('${modelIndex}','${cyclePriceIdx}')"/>
        </g:applyLayout>


    %{-- rate --}%
        <div marker="marker-${cyclePriceIdx}-flat">
            <g:applyLayout name="form/input">
                <content tag="label"><g:message code="price.strategy.TEASER_PRICING.rate"/></content>
                <content tag="label.for">model.${modelIndex}.attribute.${attributeIndex}.value</content>
                <content tag="label.class">toolTipElement</content>
                <content tag="label.title"><g:message
                        code="price.strategy.TEASER_PRICING.rate.tooltip.message"/></content>

                <g:hiddenField name="model.${modelIndex}.attribute.${attributeIndex}.name"
                               value="${TeaserPricingStrategy.PARAM_RATE_PREFIX + cyclePriceIdx}"/>
                <g:textField class="field toolTipElement" name="model.${modelIndex}.attribute.${attributeIndex++}.value"
                             value="${cyclePrice.rate}"
                             title="${message(code: 'price.strategy.TEASER_PRICING.rate.tooltip.message')}"/>
            </g:applyLayout>
        </div>

        %{-- Rate Card --}%
        <div marker="marker-${cyclePriceIdx}-rateCard">
            <g:hiddenField name="model.${modelIndex}.attribute.${attributeIndex}.name"
                           value="${TeaserPricingStrategy.PARAM_RATE_CARD_PREFIX + cyclePriceIdx}"/>
            <g:applyLayout name="form/select">
                <content tag="label"><g:message code="price.strategy.TEASER_PRICING.rateCard"/></content>
                <content tag="label.for">model.${modelIndex}.attribute.${attributeIndex}.value</content>
                <content tag="label.class">toolTipElement</content>
                <content tag="label.title"><g:message
                        code="price.strategy.TEASER_PRICING.rateCard.tooltip.message"/></content>
                <g:select class="toolTipElement"
                          title="${message(code: 'price.strategy.TEASER_PRICING.rateCard.tooltip.message')}"
                          from="${rateCards}"
                          name="model.${modelIndex}.attribute.${attributeIndex++}.value"
                          optionKey="id" optionValue="${{ it.name }}"
                          value="${StringUtils.isNotEmpty(cyclePrice.rateCard) ? cyclePrice.rateCard as Integer : cyclePrice.rateCard}"
                          noSelection="['': StringUtils.EMPTY]"/>
            </g:applyLayout>
        </div>

        %{-- Meta fields --}%
        <div marker="marker-${cyclePriceIdx}-rateCard">
            <g:applyLayout name="form/attribute">
                <content tag="header.class">toolTipElement inp4-heading</content>
                <content tag="header.name.title"><g:message
                        code="price.strategy.TEASER_PRICING.attribute.name.tooltip.message"/></content>
                <content tag="header.name"><g:message
                        code="price.strategy.TEASER_PRICING.attribute.name.header"/></content>
                <content tag="header.value.title"><g:message
                        code="price.strategy.TEASER_PRICING.attribute.value.tooltip.message"/></content>
                <content tag="header.value"><g:message
                        code="price.strategy.TEASER_PRICING.attribute.value.header"/></content>
            </g:applyLayout>

            <g:each in="${cyclePrice.metaFields.entrySet()}" var="cpMetaField" status="cpMetaFieldIdx">
                <div id="row-${modelIndex}-${cyclePriceIdx}-${cpMetaFieldIdx}">
                    <g:applyLayout name="form/attribute">
                        <content tag="name">
                            <g:hiddenField name="model.${modelIndex}.attribute.${attributeIndex}.name"
                                           value="${TeaserPricingStrategy.PARAM_METAFIELD_PREFIX + cyclePriceIdx + "." + cpMetaFieldIdx + ".name"}"/>
                            <g:textField class="field toolTipElement"
                                         title="${message(code: 'price.strategy.TEASER_PRICING.attribute.name.tooltip.message')}"
                                         name="model.${modelIndex}.attribute.${attributeIndex++}.value"
                                         value="${cpMetaField.getKey()}"/>
                        </content>
                        <content tag="value">
                            <div class="inp-bg">
                                <g:hiddenField name="model.${modelIndex}.attribute.${attributeIndex}.name"
                                               value="${TeaserPricingStrategy.PARAM_METAFIELD_PREFIX + cyclePriceIdx + "." + cpMetaFieldIdx + ".value"}"/>
                                <g:textField class="field toolTipElement"
                                             title="${message(code: 'price.strategy.TEASER_PRICING.attribute.value.tooltip.message')}"
                                             name="model.${modelIndex}.attribute.${attributeIndex++}.value"
                                             value="${cpMetaField.getValue()}"/>
                            </div>
                        </content>

                        <a id="remove-${modelIndex}-${attributeIndex}-${cpMetaFieldIdx}"
                           onclick="removeTeaserMetafield(${modelIndex}, ${cyclePriceIdx}, ${cpMetaFieldIdx})">
                            <img src="${resource(dir: 'images', file: 'remove.png')}" alt="add" class="toolTipElement"
                                 title="${message(code: 'price.strategy.COMMON.attributes.remove.tooltip.message')}"/>
                        </a>
                    </g:applyLayout>
                </div>
            </g:each>

        %{-- one empty row --}%
            <div id="row-${modelIndex}-${cyclePriceIdx}-last">
                <g:set var="cpMetaFieldIdx" value="${cyclePrice.metaFields.size()}"/>
                <g:applyLayout name="form/attribute">
                    <content tag="name">
                        <g:hiddenField name="model.${modelIndex}.attribute.${attributeIndex}.name"
                                       value="${TeaserPricingStrategy.PARAM_METAFIELD_PREFIX + cyclePriceIdx + ".-1.name"}"/>
                        <g:textField id="name-${modelIndex}-${cyclePriceIdx}" class="field toolTipElement"
                                     title="${message(code: 'price.strategy.TEASER_PRICING.attribute.name.tooltip.message')}"
                                     name="model.${modelIndex}.attribute.${attributeIndex++}.value"/>
                    </content>
                    <content tag="value">
                        <div class="inp-bg">
                            <g:hiddenField name="model.${modelIndex}.attribute.${attributeIndex}.name"
                                           value="${TeaserPricingStrategy.PARAM_METAFIELD_PREFIX + cyclePriceIdx + ".-1.value"}"/>
                            <g:textField id="value-${modelIndex}-${cyclePriceIdx}" class="field toolTipElement"
                                         title="${message(code: 'price.strategy.TEASER_PRICING.attribute.value.tooltip.message')}"
                                         name="model.${modelIndex}.attribute.${attributeIndex++}.value"/>
                        </div>
                    </content>

                    <a id="add-${modelIndex}-${attributeIndex}-${cpMetaFieldIdx}"
                       onclick="addTeaserMetafield(${modelIndex}, ${attributeIndex}, ${cpMetaFieldIdx}, ${cyclePriceIdx})">
                        <img src="${resource(dir: 'images', file: 'add.png')}" alt="remove" class="toolTipElement"
                             title="${message(code: 'price.strategy.COMMON.attributes.add.tooltip.message')}"/>
                    </a>
                </g:applyLayout>
            </div>
        </div>

        <div class="btn-row">
            <a id="remove-cycle-${modelIndex}-${cyclePriceIdx}" class="submit delete"
               onclick="removeCycle(${modelIndex}, ${cyclePriceIdx});"><span>Remove</span></a>
        </div>
    </div>
</g:each>

<div id="cycle-marker-${modelIndex}" >
</div>

<div class="btn-row">
    <a class="submit add" onclick="addCycle(${modelIndex});"><span><g:message code="price.strategy.TEASER_PRICING.add.cycle"/></span></a>
</div>

%{--
Template that will be used to add a new cycle. The following variables gets replaced
_%ATTRIDX%_ - Attribute index. Must be unique per price model.
_%ATTRIDX_1%_ - _%ATTRIDX%_ + 1
_%CPIDX%_ - Index of this CyclePrice in the TeaserPricingStrategy

--}%
<div id="teaserTemplate" style="display: none;">
    <div id="cycle-price-${modelIndex}-_%CPIDX%_">
    %{-- From Cycle --}%
        <g:applyLayout name="form/input">
            <content tag="label"><g:message code="price.strategy.TEASER_PRICING.cycle"/></content>
            <content tag="label.for">model.${modelIndex}.attribute._%ATTRIDX%_.value</content>
            <content tag="label.class">toolTipElement</content>
            <content tag="label.title"><g:message code="price.strategy.TEASER_PRICING.cycle.tooltip.message"/></content>

            <g:hiddenField name="model.${modelIndex}.attribute._%ATTRIDX%_.name"
                           value="${TeaserPricingStrategy.PARAM_CYCLE_PREFIX}_%CPIDX%_"/>
            <g:textField class="field toolTipElement" name="model.${modelIndex}.attribute._%ATTRIDX%_.value"
                         value="0"
                         title="${message(code: 'price.strategy.TEASER_PRICING.cycle.tooltip.message')}"/>
        </g:applyLayout>

    %{-- Pricing Strategy --}%
        <g:hiddenField name="model.${modelIndex}.attribute._%ATTRIDX_1%_.name"
                       value="${TeaserPricingStrategy.PARAM_PRICING_STRATEGY_PREFIX}_%CPIDX%_"/>
        <g:applyLayout name="form/select">
            <content tag="label"><g:message code="price.strategy.TEASER_PRICING.strategy"/></content>
            <content tag="label.for">model.${modelIndex}.attribute._%ATTRIDX_1%_.value</content>
            <content tag="label.class">toolTipElement</content>
            <content tag="label.title"><g:message
                    code="price.strategy.TEASER_PRICING.strategy.tooltip.message"/></content>
            <g:select id="strategy-${modelIndex}-_%CPIDX%_" class="toolTipElement"
                      title="${message(code: 'price.strategy.TEASER_PRICING.strategy.tooltip.message')}"
                      from="${com.sapienter.jbilling.server.pricing.strategy.TeaserPricingStrategy.Strategy.values()}"
                      name="model.${modelIndex}.attribute._%ATTRIDX_1%_.value"
                      valueMessagePrefix="price.strategy.TEASER_PRICING.strategy"
                      value="${TeaserPricingStrategy.Strategy.RATE_CARD}"
                      onchange="displayCorrectRows('${modelIndex}','_%CPIDX%_')"/>
        </g:applyLayout>


    %{-- rate --}%
        <div marker="marker-_%CPIDX%_-flat">
            <g:applyLayout name="form/input">
                <content tag="label"><g:message code="price.strategy.TEASER_PRICING.rate"/></content>
                <content tag="label.for">model.${modelIndex}.attribute._%ATTRIDX_2%_.value</content>
                <content tag="label.class">toolTipElement</content>
                <content tag="label.title"><g:message
                        code="price.strategy.TEASER_PRICING.rate.tooltip.message"/></content>

                <g:hiddenField name="model.${modelIndex}.attribute._%ATTRIDX_2%_.name"
                               value="${TeaserPricingStrategy.PARAM_RATE_PREFIX}_%CPIDX%_"/>
                <g:textField class="field toolTipElement" name="model.${modelIndex}.attribute._%ATTRIDX_2%_.value"
                             title="${message(code: 'price.strategy.TEASER_PRICING.rate.tooltip.message')}"/>
            </g:applyLayout>
        </div>

        %{-- Rate Card --}%
        <div marker="marker-_%CPIDX%_-rateCard">
            <g:hiddenField name="model.${modelIndex}.attribute._%ATTRIDX_3%_.name"
                           value="${TeaserPricingStrategy.PARAM_RATE_CARD_PREFIX}_%CPIDX%_"/>
            <g:applyLayout name="form/select">
                <content tag="label"><g:message code="price.strategy.TEASER_PRICING.rateCard"/></content>
                <content tag="label.for">model.${modelIndex}.attribute._%ATTRIDX_3%_.value</content>
                <content tag="label.class">toolTipElement</content>
                <content tag="label.title"><g:message
                        code="price.strategy.TEASER_PRICING.rateCard.tooltip.message"/></content>
                <g:select class="toolTipElement"
                          title="${message(code: 'price.strategy.TEASER_PRICING.rateCard.tooltip.message')}"
                          from="${rateCards}"
                          name="model.${modelIndex}.attribute._%ATTRIDX_3%_.value"
                          optionKey="id" optionValue="${{ it.name }}"
                          noSelection="['': StringUtils.EMPTY]"/>
            </g:applyLayout>
        </div>

        %{-- Meta fields --}%
        <div marker="marker-_%CPIDX%_-rateCard">
            <g:applyLayout name="form/attribute">
                <content tag="header.class">toolTipElement inp4-heading</content>
                <content tag="header.name.title"><g:message
                        code="price.strategy.TEASER_PRICING.attribute.name.tooltip.message"/></content>
                <content tag="header.name"><g:message
                        code="price.strategy.TEASER_PRICING.attribute.name.header"/></content>
                <content tag="header.value.title"><g:message
                        code="price.strategy.TEASER_PRICING.attribute.value.tooltip.message"/></content>
                <content tag="header.value"><g:message
                        code="price.strategy.TEASER_PRICING.attribute.value.header"/></content>
            </g:applyLayout>

        %{-- one empty row --}%
            <div id="row-${modelIndex}-_%CPIDX%_-last">
                <g:set var="cpMetaFieldIdx" value="0"/>
                <g:applyLayout name="form/attribute">
                    <content tag="name">
                        <g:hiddenField name="model.${modelIndex}.attribute._%ATTRIDX_4%_.name"
                                       value="${TeaserPricingStrategy.PARAM_METAFIELD_PREFIX + "_%CPIDX%_.-1.name"}"/>
                        <g:textField id="name-${modelIndex}-_%CPIDX%_" class="field toolTipElement"
                                     title="${message(code: 'price.strategy.TEASER_PRICING.attribute.name.tooltip.message')}"
                                     name="model.${modelIndex}.attribute._%ATTRIDX_4%_.value"/>
                    </content>
                    <content tag="value">
                        <div class="inp-bg">
                            <g:hiddenField name="model.${modelIndex}.attribute._%ATTRIDX_5%_.name"
                                           value="${TeaserPricingStrategy.PARAM_METAFIELD_PREFIX + "_%CPIDX%_.-1.value"}"/>
                            <g:textField id="value-${modelIndex}-_%CPIDX%_" class="field toolTipElement"
                                         title="${message(code: 'price.strategy.TEASER_PRICING.attribute.value.tooltip.message')}"
                                         name="model.${modelIndex}.attribute._%ATTRIDX_5%_.value"/>
                        </div>
                    </content>

                    <a id="add-${modelIndex}-_%ATTRIDX_4%_-${cpMetaFieldIdx}"
                       onclick="addTeaserMetafield(${modelIndex}, _%ATTRIDX_4%_, ${cpMetaFieldIdx}, _%CPIDX%_)">
                        <img src="${resource(dir: 'images', file: 'add.png')}" alt="remove" class="toolTipElement"
                             title="${message(code: 'price.strategy.COMMON.attributes.add.tooltip.message')}"/>
                    </a>
                </g:applyLayout>
            </div>
        </div>

        <div class="btn-row">
            <a id="remove-cycle-${modelIndex}-_%CPIDX%_" class="submit delete"
               onclick="removeCycle(${modelIndex}, _%CPIDX%_);"><span><g:message
                    code="price.strategy.TEASER_PRICING.remove.cycle"/></span></a>
        </div>
    </div>

</div>


%{--
Template that will be used to add a new meta field to the cycle price. The following variables gets replaced
_%ATTRIDX%_ - Attribute index. Must be unique per price model.
_%ATTRIDX_1%_ - _%ATTRIDX%_ + 1
_%CPIDX%_ - Index of this CyclePrice in the TeaserPricingStrategy
_%MFIDX%_ - Index of this meta field in the list of meta fields for the cycle price.
--}%
<div id="attrTemplate" style="display: none;">
    <div id="row-${modelIndex}-_%ATTRIDX%_-_%MFIDX%_">
        <g:applyLayout name="form/attribute">
            <content tag="name">
                <g:hiddenField name="model.${modelIndex}.attribute._%ATTRIDX%_.name"
                               value="${TeaserPricingStrategy.PARAM_METAFIELD_PREFIX + "_%CPIDX%_.name"}"/>
                <g:textField class="field toolTipElement"
                             title="${message(code: 'price.strategy.TEASER_PRICING.attribute.name.tooltip.message')}"
                             name="model.${modelIndex}.attribute._%ATTRIDX%_.value" value="_%NAME%_"/>
            </content>
            <content tag="value">
                <div class="inp-bg">
                    <g:hiddenField name="model.${modelIndex}.attribute._%ATTRIDX_1%_.name"
                                   value="${TeaserPricingStrategy.PARAM_METAFIELD_PREFIX + "_%CPIDX%_.value"}"/>
                    <g:textField class="field toolTipElement"
                                 title="${message(code: 'price.strategy.TEASER_PRICING.attribute.value.tooltip.message')}"
                                 name="model.${modelIndex}.attribute._%ATTRIDX_1%_.value" value="_%VALUE%_"/>
                </div>
            </content>

            <a id="remove-${modelIndex}-_%ATTRIDX%_-_%MFIDX%_"
               onclick="removeTeaserMetafield(${modelIndex}, _%ATTRIDX%_, _%MFIDX%_)">
                <img src="${resource(dir: 'images', file: 'remove.png')}" alt="remove" class="toolTipElement"
                     title="${message(code: 'price.strategy.COMMON.attributes.remove.tooltip.message')}"/>
            </a>
        </g:applyLayout>
    </div>
</div>

<script type="text/javascript">
    var attributeIndex = ${attributeIndex};
    var attrTemplate;
    var teaserTemplate;

    var cycleMfsize = {
    <g:each in="${cyclePrices.values()}" var="cyclePrice" status="cyclePriceIdx">
        ${cyclePriceIdx}: ${cyclePrice.metaFields.size()+1} ,
    </g:each>
        _size : ${cyclePrices.size()}
    };

    function displayCorrectRows(modelIndex, cyclePriceIdx) {
        var isFlat = $("#strategy-"+modelIndex+"-"+cyclePriceIdx).val() === '${com.sapienter.jbilling.server.pricing.strategy.TeaserPricingStrategy.Strategy.FLAT}';
        if(isFlat) {
            $("div[marker='marker-"+cyclePriceIdx+"-flat']").show();
            $("div[marker='marker-"+cyclePriceIdx+"-rateCard']").hide();
        } else {
            $("div[marker='marker-"+cyclePriceIdx+"-flat']").hide();
            $("div[marker='marker-"+cyclePriceIdx+"-rateCard']").show();
        }
    }

    function removeTeaserMetafield(modelIndex, cyclePriceIdx, cpMetaFieldIdx) {
        $("#row-"+modelIndex+"-"+cyclePriceIdx+"-"+cpMetaFieldIdx).remove();
    }

    function addTeaserMetafield(modelIndex, attributeIndex, cpMetaFieldIdx, cyclePriceIndex) {
//        console.log("addTeaserMetafield");
        var mfIdx = cycleMfsize[cyclePriceIndex];
        var attrIdx = attributeIndex++;
        var attr1Idx = attributeIndex++;
        cycleMfsize[cyclePriceIndex] = mfIdx+1;

        var template = attrTemplate.clone().html()
                .replace(/_%ATTRIDX%_/g, attrIdx)
                .replace(/_%ATTRIDX_1%_/g, attr1Idx).replace(/_%MFIDX%_/g, mfIdx)
                .replace(/_%CPIDX%_/g, cyclePriceIndex).replace(/_%NAME%_/g, $("#name-"+modelIndex+"-"+cyclePriceIndex).val())
                .replace(/_%VALUE%_/g, $("#value-"+modelIndex+"-"+cyclePriceIndex).val());

//        console.log("template="+template);
        $(template.trim()).insertBefore("#row-"+modelIndex+"-"+cyclePriceIndex+"-last");

        $("#name-"+modelIndex+"-"+cyclePriceIndex).val("");
        $("#value-"+modelIndex+"-"+cyclePriceIndex).val("");

        $('#remove-'+modelIndex+'-'+attrIdx+'-'+cpMetaFieldIdx).click(function() {
            removeTeaserMetafield(modelIndex, cyclePriceIndex, mfIdx);
        });
    }

    function addCycle(modelIndex) {
        var cyclePriceIndex = cycleMfsize._size++;
        var mfIdx = 0;
        var attrIdx = attributeIndex++;
        var attr1Idx = attributeIndex++;
        var attr2Idx = attributeIndex++;
        var attr3Idx = attributeIndex++;
        var attr4Idx = attributeIndex++;
        var attr5Idx = attributeIndex++;
        cycleMfsize[cyclePriceIndex] = mfIdx+1;

        var template = teaserTemplate.clone().html()
                .replace(/_%ATTRIDX%_/g, attrIdx).replace(/_%ATTRIDX_1%_/g, attr1Idx)
                .replace(/_%ATTRIDX_2%_/g, attr2Idx).replace(/_%ATTRIDX_3%_/g, attr3Idx)
                .replace(/_%ATTRIDX_4%_/g, attr4Idx).replace(/_%ATTRIDX_5%_/g, attr5Idx)
                .replace(/_%MFIDX%_/g, mfIdx)
                .replace(/_%CPIDX%_/g, cyclePriceIndex);

        $(template.trim()).insertBefore("#cycle-marker-${modelIndex}");

        displayCorrectRows('${modelIndex}', cyclePriceIndex);
    }

    function removeCycle(modelIndex, cyclePriceIdx) {
        $("#cycle-price-"+modelIndex+"-"+cyclePriceIdx).remove();
    }

    $(document).ready(function() {
        for(idx=0; idx<cycleMfsize._size; idx++) {
            displayCorrectRows(${modelIndex}, idx);
        }

        $('#attrTemplate').find('.dynamicAttrs').removeClass().addClass("product-price-attribute-dynamic");
        $('#teaserTemplate').find('.dynamicAttrs').removeClass().addClass("product-price-attribute-dynamic");
        attrTemplate = $("#attrTemplate").detach();
        teaserTemplate = $("#teaserTemplate").detach();
    });

</script>