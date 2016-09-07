/*
 * JBILLING CONFIDENTIAL
 * _____________________
 *
 * [2003] - [2013] Enterprise jBilling Software Ltd.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Enterprise jBilling Software.
 * The intellectual and technical concepts contained
 * herein are proprietary to Enterprise jBilling Software
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden.
 */
package jbilling

import com.sapienter.jbilling.common.Constants
import grails.plugin.springsecurity.annotation.Secured

@Secured(["isFullyAuthenticated()"])
class MyAccountController {
	static scope = "prototype"
    def breadcrumbService

    def index () {
        breadcrumbService.addBreadcrumb("myAccount", "index", null, null)

        if(session['main_role_id'] == Constants.TYPE_CUSTOMER) {
            flash.altView = '/myAccount/showCustomer'
            chain controller: "customer", action: "show", id:session['user_id']
        } else {
            flash.altView = '/myAccount/showUser'
            chain controller: "user", action:"show", id:session['user_id']
        }
    }

    def editUser () {
        flash.altView = '/myAccount/editUser'
        chain controller: "user", action:"edit", id:session['user_id']
    }

}
