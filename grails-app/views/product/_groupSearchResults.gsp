%{--
  JBILLING CONFIDENTIAL
  _____________________

  [2003] - [2013] Enterprise jBilling Software Ltd.
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Enterprise jBilling Software.
  The intellectual and technical concepts contained
  herein are proprietary to Enterprise jBilling Software
  and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden.
  --}%
<%@ page import="com.sapienter.jbilling.client.util.SortableCriteria; java.util.regex.Pattern" %>

<%--
   Filters for searching asset which will be added to groups

  @author Gerhard Maree
  @since  18-Jul-2013
--%>
<%-- parameters the page functionality must include in URLs --%>
<g:set var="searchParams" value="${SortableCriteria.extractParameters(params, ['filterBy', Pattern.compile(/search.*/),  Pattern.compile(/filterByMetaFieldId(\d+)/), Pattern.compile(/filterByMetaFieldValue(\d+)/)])}" />

<div class="table-box">
    <table id="users" cellspacing="0" cellpadding="0">
        <thead>
        <tr>
            <th>
                    <g:message code="asset.table.th.identifier"/>
            </th>
            <th class="medium2">
                    <g:message code="asset.table.th.creationDate"/>
            </th>
            <th class="small">
                    <g:message code="asset.table.th.status"/>
            </th>
        </tr>
        </thead>

        <tbody>
        <g:each in="${assets}" var="asset">
            <tr id="group-asset-${asset.id}" class="${selected?.id == asset.id ? 'active' : ''}" >
                <td class="narrow" >
                        <em class="narrow">${asset?.identifier}</em>
                </td>
                <td class="narrow">
                        <span class="narrow"><g:formatDate format="dd-MM-yyyy HH:mm" date="${asset.createDatetime}"/></span>
                </td>
                <td class="narrow">
                        <span class="narrow">${asset.assetStatus?.description}</span>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>

<div class="pager-box">
    <div class="row">
        <div class="results">
            <g:render template="/layouts/includes/pagerShowResults" model="[steps: [10, 20, 50], action: 'groupAssetSearch', update: 'asset-search-results', searchParams: searchParams]" />
        </div>
    </div>

    <div class="row">
        <util:remotePaginate controller="product" action="groupAssetSearch" params="${searchParams}" total="${assets?.totalCount ?: 0}" update="asset-search-results"/>
    </div>
</div>