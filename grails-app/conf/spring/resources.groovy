/*
 jBilling - The Enterprise Open Source Billing System
 Copyright (C) 2003-2011 Enterprise jBilling Software Ltd. and Emiliano Conde

 This file is part of jbilling.

 jbilling is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 jbilling is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with jbilling.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.mchange.v2.c3p0.ComboPooledDataSource
import org.springframework.jdbc.core.JdbcTemplate
import com.sapienter.jbilling.common.Util

beans = {

    /*
        Database configuration
     */
    dataSource(ComboPooledDataSource) { bean ->
        bean.destroyMethod = 'close'

        // database connection properties from DataSource.groovy
        user        = grailsApplication.config.dataSource.username
        password    = grailsApplication.config.dataSource.password
        driverClass = grailsApplication.config.dataSource.driverClassName
        jdbcUrl     = grailsApplication.config.dataSource.url

        // Connection pooling using c3p0
        acquireIncrement = 2
        initialPoolSize = 10
        minPoolSize = 10
        maxPoolSize = 50
        maxIdleTime = 300
        checkoutTimeout = 10000

        /*
           Periodically test the state of idle connections and validate connections on checkout. Handles
           potential timeouts by the database server. Increase the connection idle test period if you
           have intermittent database connection issues.
         */
        testConnectionOnCheckout = false
        idleConnectionTestPeriod = 30        
		preferredTestQuery = "/* ping */ SELECT 1"
		
        /*
           Destroy un-returned connections after a period of time (in seconds) and throw an exception
           that shows who is still holding the un-returned connection. Useful for debugging connection
           leaks.
         */
        // unreturnedConnectionTimeout = 10
        // debugUnreturnedConnectionStackTraces = true
    }

    jdbcTemplate(JdbcTemplate) {
        dataSource = ref('dataSource')
    }

    if (Util.getSysPropBooleanTrue("hbase.audit.logging")) {

        // [2014-12-04 igor.poteryaev] obsoleted loggind using interceptor

        // println "HBase Audit is enabled. Registering interceptor"
        // eventTriggeringInterceptor(com.sapienter.jbilling.server.audit.hibernate.interceptor.AuditInterceptor)


        // [2014-12-04 igor.poteryaev] instead will use officially documented in "Hibernate Events" section of
        // http://grails.org/doc/latest/guide/GORM.html#eventsAutoTimestamping
        // approach using listeners

        println "HBase Audit is enabled. Registering listeners"
        auditListener(com.sapienter.jbilling.server.audit.hibernate.AuditEventListener) { bean ->
            bean.autowire = 'byName'
        }

        hibernateEventListeners(org.codehaus.groovy.grails.orm.hibernate.HibernateEventListeners) {
            listenerMap = ['post-insert': auditListener,
                           'post-update': auditListener,
                           'post-delete': auditListener]
        }
    } else {
        println "HBase Audit is disabled."
    }

    /*
        Custom data binding and property parsing rules
     */
    customPropertyEditorRegistrar(com.sapienter.jbilling.client.editor.CustomPropertyEditorRegistrar) {
        messageSource = ref('messageSource')
    }

    /*
        Spring security
     */
    // populates session attributes and locale from the authenticated user
    securitySession(com.sapienter.jbilling.client.authentication.util.SecuritySession) {
        localeResolver = ref('localeResolver')
    }

    // normal username / password authentication
    authenticationProcessingFilter(com.sapienter.jbilling.client.authentication.CompanyUserAuthenticationFilter) {
        authenticationManager = ref("authenticationManager")        
        authenticationSuccessHandler = ref('authenticationSuccessHandler')
        authenticationFailureHandler = ref('authenticationFailureHandler')
        rememberMeServices = ref('rememberMeServices')
        securitySession = ref('securitySession')
    }

    /*
        Automatic authentication using a defined username and password that removes the need for the caller
        to authenticate themselves. This is used with web-service protocols that don't support authentication,
        but can also be used to create "pre-authenticated" URLS by updating the filter chain in 'Config.groovy'.
     */
    staticAuthenticationProcessingFilter(com.sapienter.jbilling.client.authentication.StaticAuthenticationFilter) {
        authenticationManager = ref("authenticationManager")
        authenticationDetailsSource = ref('authenticationDetailsSource')
        username = "admin;1"
        password = "123qwe"
    }

    
    /*
        Stateless SecurityContextPersistenceFilter disables creation of a session for the requests. It is used for the API calls.
        To use it, updating the filter chain in 'Config.groovy' is necessary.
     */
    statelessHttpSessionSecurityContextRepository(org.springframework.security.web.context.HttpSessionSecurityContextRepository){
        allowSessionCreation = false;
    }

    statelessSecurityContextPersistenceFilter(org.springframework.security.web.context.SecurityContextPersistenceFilter){
        securityContextRepository = ref("statelessHttpSessionSecurityContextRepository")
    }

    jbillingUserService(com.sapienter.jbilling.client.authentication.AuthenticationUserService) {
    // [2016-03-10 igor.poteryaev@jbilling.com] Commented out as part of fix for: https://appdirect.jira.com/browse/JB-313
    // userService = ref("userService")
        springSecurityService = ref("springSecurityService")
    }
    
    userDetailsService(com.sapienter.jbilling.client.authentication.CompanyUserDetailsService) {
        userService = ref("jbillingUserService")
    }

    //used as password encoder router to get to the real password encoder that will do the job
    passwordEncoder(com.sapienter.jbilling.client.authentication.JBillingPasswordEncoder){
        userService = ref("jbillingUserService")
    }

    plainTextPasswordEncoder(org.springframework.security.authentication.encoding.PlaintextPasswordEncoder)
    md5PasswordEncoder(org.springframework.security.authentication.encoding.Md5PasswordEncoder)
    sha1PasswordEncoder(org.springframework.security.authentication.encoding.ShaPasswordEncoder, 1)
    sha256PasswordEncoder(org.springframework.security.authentication.encoding.ShaPasswordEncoder, 256)
    bCryptPasswordEncoder(grails.plugin.springsecurity.authentication.encoding.BCryptPasswordEncoder, 10)
	
	saltSource(com.sapienter.jbilling.client.authentication.JBillingSaltSource)

    permissionVoter(com.sapienter.jbilling.client.authentication.PermissionVoter)

    webExpressionVoter(com.sapienter.jbilling.client.authentication.SafeWebExpressionVoter) {
        expressionHandler = ref("webExpressionHandler")
    }
	
	diameterUserLocator(com.sapienter.jbilling.server.diameter.UserLocatorByMetaField) {
		fieldName = "Subscription-Id-Data"
		metaFieldName = "Subscriber URI"
	}
	
	diameterItemLocator(com.sapienter.jbilling.server.diameter.ItemLocatorByInternalNumber) {
		fieldName = "Rating-Group"
	}
	
	diameterHelper(com.sapienter.jbilling.server.diameter.SessionHelper) {
		entityId = 10
		userLocator = ref("diameterUserLocator")
		itemLocator = ref("diameterItemLocator")
	}
	
	diameterServer(com.sapienter.jbilling.server.diameter.ChargingServer) {
		configurationFile = "/jdiameter-config.xml"
	}

    appAuthResultHandler(com.sapienter.jbilling.client.authentication.AuthenticationResultHandler) {
        userSession = ref("userSession")
    }

    /*
        Remoting
     */
    // HTTP request handler for remote beans
    httpRequestAdapter org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter


    /*
        Others
     */
    // resolves exceptions into messages for the view
    viewUtils(com.sapienter.jbilling.client.ViewUtils) {
        messageSource = ref("messageSource")
    }

    // bean for managing the scheduled background processes
    schedulerBootstrapHelper(com.sapienter.jbilling.server.util.SchedulerBootstrapHelper) { bean ->
        bean.singleton = true
    }
	
	// encryption
	dataEncrypter ( com.sapienter.jbilling.server.util.PlainDataEncrypter )

    /*
        Debugging
     */
    //The listener will print all the old and new values for a object before it gets updated by hibernate.
    /*
    debugHibernateListener(HibernateEventListener)

    hibernateEventListeners(HibernateEventListeners) {
        listenerMap = [ 'pre-update': debugHibernateListener ]
    }
    */
//  This has been moved to the resources.xml, active mq listener are not set in the right way using this
//    if (Util.getSysPropBooleanTrue("process.run_jmr_processor")) {
//        println "Running JMR Processor"
//        importBeans("classpath:slave/jms-remote-chunking-slave.xml")
//    } else {
//        println "NOT running JMR Processor"
//    }
}
