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

<%@ page import="com.sapienter.jbilling.server.pricing.db.PriceModelStrategy" %>

<%--
  Product price strategy filter

  @author Brian Cowdery
  @since 11-Apr-2011
--%>

<div id="${filter.name}">
    <span class="title <g:if test='${filter.value}'>active</g:if>"><g:message code="filters.${filter.field}.title"/></span>
    <g:remoteLink class="delete" controller="filter" action="remove" params="[name: filter.name]" update="filters"/>
    
    <div class="slide">
        <fieldset>
            <div class="input-row">
                <div class="select-bg">
                    <g:select name="filters.${filter.name}.stringValue"
                            value="${filter.stringValue}"
                            from="${PriceModelStrategy.values()*.name()}"
                            valueMessagePrefix="price.strategy"
                            noSelection="['': message(code: 'filters.' + filter.field + '.empty')]" />

                </div>
                <label for="filters.${filter.name}.stringValue"><g:message code="filters.${filter.field}.label"/></label>
            </div>
        </fieldset>
    </div>
</div>

