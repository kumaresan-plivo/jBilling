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

import com.sapienter.jbilling.csrf.RequiresValidFormToken
import grails.plugin.springsecurity.annotation.Secured

@Secured(["isAuthenticated()"])
class TabConfigController {
	static scope = "prototype"

    def index () {
        redirect action: "show"
    }

    def show () {
        def tabConfiguration = session[TabConfigurationService.SESSION_USER_TABS]
        println"tabConfiguration   " + tabConfiguration
        [tabConfigurationTabs: tabConfiguration.tabConfigurationTabs]
    }

    @RequiresValidFormToken
    def save () {
        TabConfiguration tabConfiguration = session[TabConfigurationService.SESSION_USER_TABS]
        //we have to refresh the user's tabs if he is not new, otherwise hibernate complains about stale objects
        //if we have previously added or removed items to the TabConfiguration
        boolean isPrevSaved = (tabConfiguration.id != null);
        if(isPrevSaved) tabConfiguration.refresh()
        tabConfiguration.tabConfigurationTabs.clear()
        tabConfiguration.save(flush: true)
        if (isPrevSaved) {
            TabConfigurationTab.where {tabConfiguration == tabConfiguration}.deleteAll()
        }

        def idx = 0
        for (configId in params["visible-order"].tokenize(",")) {
            def tabConfigTab = new TabConfigurationTab(
                    [   tab: Tab.get(configId as Long),
                        visible: true,
                        displayOrder: idx++
                    ]
            )
            tabConfiguration.addToTabConfigurationTabs( tabConfigTab ).save(flush: true)
        }
        for (configId in params["hidden-order"].tokenize(",")) {
            def tabConfigTab = new TabConfigurationTab(
                    [   tab: Tab.get(configId as Long),
                        visible: false,
                        displayOrder: idx++
                    ]
            )
            tabConfiguration.addToTabConfigurationTabs( tabConfigTab ).save(flush: true)
        }
        tabConfiguration.save(flush: true)
        session[TabConfigurationService.SESSION_USER_TABS] = tabConfiguration
        redirect action: "show"
    }
}
