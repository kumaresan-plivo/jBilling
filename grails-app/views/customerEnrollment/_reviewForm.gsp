<g:if test="${accountInformationTypes && accountInformationTypes.size()>0}">
    <g:each in="${accountInformationTypes}" var="ait">
        <div class="form-columns" id="ait-inner-4">

            <h3>${ait.getName()}</h3>
            <hr/>

            <%
                def aitMetaFields =  ait?.metaFields?.sort { it.displayOrder }
                def leftColumnFields = []
                def rightColumnFields = []
                aitMetaFields.eachWithIndex { field, index ->
                    def fieldValue = metaFields.find {
                        mfv -> mfv.field.id == field.id }

                    if(fieldValue){
                        if(index > aitMetaFields.size()/2){
                            rightColumnFields << fieldValue
                        } else {
                            leftColumnFields << fieldValue
                        }
                    }
                }
            %>

            <div class="column" >
            <g:if test="${leftColumnFields.size() > 0}">
                <g:each in="${leftColumnFields}" var="varMetaField" >
                    <g:render template="/metaFields/displayMetaField"
                              model="[metaField : varMetaField]"/>
                </g:each>
            </g:if>
        </div>

            <div class="column">
                <g:if test="${rightColumnFields.size() > 0}">
                    <g:each in="${rightColumnFields}" var="varMetaField" >
                        <g:render template="/metaFields/displayMetaField"
                                  model="[metaField : varMetaField]"/>
                    </g:each>
                </g:if>
            </div>
        </div>

    </g:each>
</g:if>



