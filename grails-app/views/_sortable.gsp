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

<g:remoteLink controller="${controller}" action="${action}" id="${id}"
          params="${sortableParams(params: [partial: true, max: params.max,_eventId: eventId,  offset: params.offset], sort: sort, order: order, alias: alias, fetch: fetch) + searchParams ?: [:]}"
        update="${update}" method="${method}">
    ${body}
    <img src="${resource(dir: 'images', file: order ? 'arrows-' + order + '.gif' : 'arrows.gif')}" alt="sort"/>
</g:remoteLink>