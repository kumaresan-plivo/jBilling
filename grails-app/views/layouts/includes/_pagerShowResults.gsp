<%@ page import="com.sapienter.jbilling.client.util.SortableCriteria" %>
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

<g:message code="pager.show.max.results"/>

<g:each var="max" in="${steps}">
    <g:if test="${params.max == max}">
        <span>${max}</span>
    </g:if>
    <g:else>
        <g:set var="extraParams" value="${extraParams?extraParams:[:]}"/>
        <g:remoteLink onSuccess="hideResults();" action="${action ?: 'list'}" id="${id}" params="${sortableParams(params: [partial: true, max: max, id: id, contactFieldTypes: contactFieldTypes ?: null] + extraParams)}" update="${update}">${max}</g:remoteLink>
    </g:else>
</g:each>

<script type="text/javascript">
    function hideResults(){
        <g:if test="${action != 'subaccounts'}">
            $('#column2').html('');
        </g:if>
    }
</script>
