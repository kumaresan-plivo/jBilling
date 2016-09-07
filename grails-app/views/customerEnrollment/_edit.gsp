<g:each in="${accountInformationTypes}" var="ait" status="i">
    <div id="tabs-${i}" style="display: none">
        <g:render template="/customer/aITMetaFields"
                  model="[ait: ait, aitVal: ait.id, values: customerEnrollment?.metaFields]"/>

        <div class="btn-box order-btn-box form-columns" style="width: 20%">
        <div style="width: 45%; display: inline-block; float: left">
            <g:if test="${i > 0}">
                <a href="javascript:void(0)" class="submit previous prev-btn">
                    <span><g:message code="wizard.previous"/></span>
                </a>
            </g:if>

        </div>

        <div style="width: 45%;display: inline; float: right">
            <g:if test="${i < accountInformationTypes.size() - 1}">
                <a href="javascript:void(0)" class="submit next next-btn">
                    <span><g:message code="wizard.next"/></span>
                </a>
            </g:if>
        </div>
    </div>
    </div>
</g:each>

<div id="tabs-${accountInformationTypes.size() + 1}">
    <g:if test="${enrollment}">
        <div class="content">
            <g:if test="${flash.info}">
                <div id="messages">
                    <br/>
                    <div class="msg-box info">
                        <img src="${resource(dir:'images', file: "icon34.gif")}" alt="Information">
                        <strong>${flash.info}</strong>
                    </div>
                </div>
            </g:if>
            <g:render template="/customerEnrollment/reviewForm" model="[accountInformationTypes:accountInformationTypes, metaFields:enrollment.getMetaFields()]"/>
        </div>

    </g:if>

    <div class="btn-box order-btn-box"
         style="width: 20%; margin-left: auto; margin-right: auto;">
        <a href="javascript:void(0)" class="submit edit prev-btn">
            <span><g:message code="customer.enrollment.edit.button"/></span>
        </a>
    </div>

</div>