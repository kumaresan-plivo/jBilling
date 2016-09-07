<%@ page import="com.sapienter.jbilling.server.user.db.UserDTO" %>
<div class="column-hold">
    <div class="heading">
        <strong>
            <g:message code="customer.enrollment.show.heading" />

        </strong>
    </div>

    <!-- Customer Enrollment Details -->
    <div class="box">

        <div class="sub-box">
            <table class="dataTable" cellspacing="0" cellpadding="0">
                <tbody>
                <tr>
                    <td><g:message code="customer.enrollment.id"/></td>
                    <td class="value">${customerEnrollment?.id}</td>
                </tr>
                <tr>
                    <td><g:message code="customer.enrollment.show.company"/></td>
                    <td class="value">${customerEnrollment?.companyName}</td>
                </tr>
                <tr>
                    <td><g:message code="customer.enrollment.account.type"/></td>
                    <td class="value">
                        ${customerEnrollment?.accountTypeName}
                    </td>
                </tr>
                <tr>
                    <td><g:message code="customer.enrollment.status"/></td>
                    <td class="value">${customerEnrollment?.status}</td>
                </tr>
                <sec:ifAllGranted roles="EDI_922">
                    <g:if test="${customerEnrollment?.customerId}">
                        <tr>
                            <td><g:message code="customer.enrollment.label.customer.id"/></td>
                            <td class="value">
                                <g:remoteLink controller="customer" action="show" id="${customerEnrollment?.customerId}"
                                              before="register(this);" onSuccess="render(data, next);">
                                    Customer-${customerEnrollment?.customerId}
                                </g:remoteLink>
                            </td>
                        </tr>
                    </g:if>
                </sec:ifAllGranted>
                <tr>
                    <td><g:message code="customer.enrollment.edi.files"/></td>
                    <td class="value">
                        <g:link controller="ediFile" action="list" params="[enrollmentId:customerEnrollment?.id]"><g:message code="customer.enrollment.edi.files"/></g:link>
                    </td>
                </tr>

                <g:if test="${customerEnrollment?.parentEnrollmentId}">
                    <tr>
                        <td><g:message code="customer.enrollment.parent.enrollment.id"/></td>
                        <td class="value">${customerEnrollment?.parentEnrollmentId}</td>
                    </tr>

                </g:if>

                <g:if test="${customerEnrollment?.parentUserId}">
                    <tr>
                        <td><g:message code="customer.enrollment.parent.userId.id"/></td>
                        <td class="value"> ${customerEnrollment?.parentUserId}</td>
                    </tr>
                </g:if>

                </tbody>
            </table>
        </div>
    </div>

    <div class="heading">
        <strong>
            <g:message code="customer.enrollment.show.details" />
        </strong>
    </div>
    <div class="box">

        <div class="sub-box">
            <g:each in="${accountInformationTypes?.sort{ it.displayOrder }}" var="metaFieldGroup">
                <div>${metaFieldGroup.name}</div>
                <table cellspacing="0" cellpadding="0" class="dataTable">
                    <tbody>
                    <g:each in="${metaFieldGroup?.metaFields?.sort{ it.displayOrder }}" var="metaField">

                        <g:set var="fieldValue" value="${customerEnrollment.metaFields?.find{ (it.fieldName == metaField.name) &&
                                ((it.groupId && metaFieldGroup.id && it.groupId == metaFieldGroup.id) || (!it.groupId && !metaFieldGroup.id)) }?.getValue()}"/>
                        <g:if test="${fieldValue == null && metaField.getDefaultValue()}">
                            <g:set var="fieldValue" value="${metaField.getDefaultValue().getValue()}"/>
                        </g:if>
                        <tr>
                            <td>
                                ${metaField?.name}
                            </td>
                            <td class="value">   ${fieldValue?fieldValue:''}</td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </g:each>
        </div>
    </div>

    <g:if test="${customerEnrollment?.customerEnrollmentComments}">
        <div class="heading">
            <strong>
                <g:message code="customer.enrollment.comment" />
            </strong>
        </div>

        <div class="box">
            <div class="sub-box">
                <div class="table-box">
                    <table id="users" cellspacing="0" cellpadding="0">
                        <thead>
                        <tr class="ui-widget-header" >
                            <th width="50px"><g:message code="customer.detail.note.form.author"/></th>
                            <th width="60px"><g:message code="customer.detail.note.form.createdDate"/></th>
                            <th width="150px"><g:message code="customer.enrollment.comment"/></th>
                        </thead>
                        <tbody>

                        <g:if test="${customerEnrollment?.customerEnrollmentComments}">
                            <g:each in="${customerEnrollment?.customerEnrollmentComments}" var="comment">

                                <tr>
                                    <td>${comment?.userName}</td>
                                    <td><g:formatDate date="${comment?.dateCreated}" type="date" style="MEDIUM"/>  </td>
                                    <td>${comment?.comment}</td>
                                </tr>
                            </g:each>
                        </g:if>
                        <g:else>
                            <p><em><g:message code="customer.detail.note.empty.message"/></em></p>
                        </g:else>
                        </tbody>
                    </table>
                </div>

            </div>
        </div>

    </g:if>

    <div class="btn-box">
        <sec:ifAllGranted roles="CUSTOMER_ENROLLMENT_911">
            <g:if test="${customerEnrollment.status==com.sapienter.jbilling.server.customerEnrollment.CustomerEnrollmentStatus.PENDING || customerEnrollment.status==com.sapienter.jbilling.server.customerEnrollment.CustomerEnrollmentStatus.REJECTED}">
                <div class="row">
                    <g:link action="edit" id="${customerEnrollment.id}" class="submit edit"><span><g:message code="button.edit"/></span></g:link>
                </div>
            </g:if>

        </sec:ifAllGranted>
        <div >
            <sec:ifAllGranted roles="CUSTOMER_ENROLLMENT_912">
                <a onclick="showConfirm('delete-${customerEnrollment.id}');" class="submit delete"><span><g:message code="button.delete"/></span></a>
            </sec:ifAllGranted>
        </div>
    </div>
    <g:render template="/confirm"
              model="['message': 'customer.enrollment.delete.confirm',
                      'controller': 'customerEnrollment',
                      'action': 'delete',
                      'id': customerEnrollment?.id,
              ]"/>

</div>

