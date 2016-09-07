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

grails.work.dir = "${userHome}/.grails/${grailsVersion}"
grails.project.work.dir = "${grails.work.dir}/projects/${appName}-${appVersion}"

grails.servlet.version             = "3.0"
grails.project.class.dir           = "target/classes"
grails.project.test.class.dir      = "target/test-classes"
grails.project.test.reports.dir    = "target/test-results"
grails.project.target.level        = 1.8
grails.project.source.level        = 1.8
grails.project.war.file            = "target/${appName}.war"
grails.project.dependency.resolver = "maven" // or ivy

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
    }

    // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    log "debug"

    // repositories for dependency resolution
    repositories {
        inherits true       // inherit repositories from plugins
        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()        // maven repositories
        mavenRepo "http://maven.jbilling.com/nexus/content/repositories/releases"
        mavenRepo "http://repo.jenkins-ci.org/releases/"
        mavenCentral()
    }

    dependencies {

        // 'build' phase dependencies

        build 'org.eclipse.jdt:core:3.3.0-v_771' // for drools-compile

        build ('com.lowagie:itext:2.1.7') {
            transitive = false
        }
        build 'org.eclipse.jdt.core.compiler:ecj:4.4.2'  // for tomcat8 plugin and jasperreports

        // Exclude old, obsoleted dependencies
        build ("log4j:log4j:1.2.17") {
            transitive = false
        }


        // 'compile' phase dependencies
        compile('org.springmodules:spring-modules-cache:0.8') {
            transitive = false
        }
        compile('org.osgi:org.osgi.core:4.1.0')
        compile('org.apache.xbean:xbean-spring:3.5') {
            excludes 'commons-logging'
        }


        // 'compile' phase dependencies

        compile('org.springmodules:spring-modules-cache:0.8') {
            transitive = false
        }

        compile('org.apache.xmlrpc:xmlrpc-client:3.1') {
            excludes 'junit', 'xml-apis'
        }

        compile('org.apache.geronimo.javamail:geronimo-javamail_1.4_mail:1.8.4')
        compile('org.apache.geronimo.javamail:geronimo-javamail_1.4_provider:1.8.4')
        compile('org.apache.geronimo.specs:geronimo-javamail_1.4_spec:1.7.1')

        def droolsVersion = "5.0.1"
        compile ("org.drools:drools-core:${droolsVersion}",
                 "org.drools:drools-decisiontables:${droolsVersion}",
                 "org.drools:drools-templates:${droolsVersion}") {
            excludes 'joda-time'
        }
        compile ("org.drools:drools-ant:${droolsVersion}") {
            excludes 'joda-time', "ant", "ant-nodeps"
        }
        compile ("org.drools:drools-compiler:${droolsVersion}") {
            excludes 'joda-time', 'core'
        }

        compile('org.quartz-scheduler:quartz:2.2.2') {
            excludes "c3p0"
        }
        compile 'joda-time:joda-time:2.9'

        compile('net.sf.opencsv:opencsv:2.3') {
            excludes 'junit'
        }

        compile('commons-httpclient:commons-httpclient:3.0.1') {
            excludes 'junit'
        }
        compile 'commons-net:commons-net:3.3'
        compile 'commons-codec:commons-codec:1.10'
        compile 'commons-beanutils:commons-beanutils:1.9.2'
        compile 'commons-configuration:commons-configuration:1.10'

        compile 'org.hibernate:hibernate-validator:5.1.2.Final'

        def jacksonVersion = "2.7.4"
        compile "com.fasterxml.jackson.core:jackson-core:${jacksonVersion}"
        compile "com.fasterxml.jackson.core:jackson-databind:${jacksonVersion}"
        compile "com.fasterxml.jackson.core:jackson-annotations:${jacksonVersion}"

        compile 'org.apache.velocity:velocity:1.7'
        compile('org.apache.velocity:velocity-tools:2.0') {
            excludes 'struts-core', 'struts-taglib', 'struts-tiles'
        }

        compile('net.sf.jasperreports:jasperreports:5.6.1') {
            excludes 'jaxen', 'xalan', 'xml-apis', 'jdtcore', 'itext'
        }

        compile "org.bouncycastle:bcprov-jdk16:1.46"

        compile 'net.sf.jasperreports:jasperreports-fonts:5.6.1'
        compile 'org.apache.poi:poi:3.6'

        compile('net.sf.barcode4j:barcode4j:2.1') {
            excludes 'xerces', 'xalan', 'xml-apis', "ant"
        }

        compile 'org.liquibase:liquibase-core:3.2.3'
        compile 'com.mchange:c3p0:0.9.5.1'

        compile('org.codehaus.groovy.modules.http-builder:http-builder:0.5.2') {
            excludes "commons-logging", "xml-apis", "groovy"
        }

        compile "com.thoughtworks.xstream:xstream:1.4.7"
        compile "org.mockftpserver:MockFtpServer:2.6"

        // lombok dependency for boiler plate, getter,setter, toString and hashCode method
        compile 'org.projectlombok:lombok:1.16.6'

        def springVersion = "4.0.9.RELEASE"
        compile "org.springframework:spring-beans:${springVersion}"
        compile "org.springframework:spring-webmvc:${springVersion}"
        compile "org.springframework:spring-orm:${springVersion}"
        // [igor.poteryaev@jbilling.com 2016-05-06] 
        // Updated to fix: https://jira.spring.io/browse/SPR-11841
        // (indefinite wait on jbilling instance shutdown due to race condition in spring-jms module)
        compile("org.springframework:spring-core:4.2.5.RELEASE") {
            transitive = false
        }
        compile("org.springframework:spring-jms:4.2.5.RELEASE") {
            transitive = false
        }
        compile("org.springframework:spring-messaging:4.2.5.RELEASE") {
            transitive = false
        }

        def springIntegrationVersion = "4.0.8.RELEASE"
        compile "org.springframework.integration:spring-integration-core:${springIntegrationVersion}"
        compile "org.springframework.integration:spring-integration-ftp:${springIntegrationVersion}"
        compile "org.springframework.integration:spring-integration-sftp:${springIntegrationVersion}"
        compile "org.springframework.integration:spring-integration-file:${springIntegrationVersion}"
        compile "org.springframework.integration:spring-integration-jdbc:${springIntegrationVersion}"
        compile "org.springframework.integration:spring-integration-jms:${springIntegrationVersion}"

        def springBatchVersion = "3.0.5.RELEASE"
        compile "org.springframework.batch:spring-batch-core:${springBatchVersion}"
        compile "org.springframework.batch:spring-batch-integration:${springBatchVersion}"

        compile 'org.grails:grails-datastore-core:3.1.2.RELEASE'

        compile('org.springframework.data:spring-data-jpa:1.8.1.RELEASE') {
            excludes "aspectjrt" // included by grails.plugins.rest
        }

        compile 'org.apache.httpcomponents:httpclient:4.5.1'

        compile 'eu.infomas:annotation-detector:3.0.5'

        // 'runtime' phase dependencies

        runtime 'org.hibernate:hibernate-entitymanager:4.3.5.Final'

        runtime 'javax.activation:activation:1.1.1'

        def activemqVersion = "5.3.2"
        runtime "org.apache.activemq:activemq-all:${activemqVersion}"
        runtime("org.apache.activemq:activemq-pool:${activemqVersion}") {
            excludes 'junit', 'commons-logging', 'log4j'
        }

        runtime 'org.eclipse.jdt.core.compiler:ecj:4.4.2'  // for tomcat8 plugin and jasperreports

        //needed by the jDiameter library
        runtime 'org.picocontainer:picocontainer:2.13.5'
        runtime 'commons-pool:commons-pool:1.6'

        runtime 'xerces:xercesImpl:2.11.0'  // for paypal payment

        runtime 'org.postgresql:postgresql:9.3-1102-jdbc41'
        runtime 'mysql:mysql-connector-java:5.1.26'
        runtime 'org.hsqldb:hsqldb:2.3.2'

        runtime ('com.lowagie:itext:2.1.7') {
            transitive = false
        }

        runtime 'com.googlecode.jcsv:jcsv:1.4.0'
        runtime 'org.apache.tika:tika-core:1.11'


        // 'provided' dependencies

        provided 'javax.jms:jms-api:1.1-rev-1'


        // Test dependencies

        // override junit bundled with grails
        build('junit:junit:4.12') {
            transitive = false
        }
        test ('junit:junit:4.12') {
            transitive = false // excludes "hamcrest-core"
        }
        test    'org.hamcrest:hamcrest-all:1.3'

        test    ('org.testng:testng:6.9.10') {
            excludes "junit", "snakeyaml", "bsh"
        }
        test    ('org.easymock:easymockclassextension:3.2') {
            excludes "cglib-nodep"
        }

        // minimal viable selenium dependencies
        // without transitive = false more than 25 dependent libraries will be used
        def seleniumVersion = "2.53.0"
        test    ("org.seleniumhq.selenium:selenium-java:${seleniumVersion}") {
            transitive = false
        }
        test    ("org.seleniumhq.selenium:selenium-server-standalone:${seleniumVersion}") {
            transitive = false
        }

        test "org.glassfish.web:el-impl:2.2"

        test "org.jacoco:org.jacoco.ant:0.7.5.201505241946"

        test "com.googlecode.json-simple:json-simple:1.1.1"


        // jbilling modules

        compile ('com.sapienter.jbilling:audit-service:1.0.0') {
            transitive = false
        }
        compile ('com.sapienter.jbilling:customer-service:1.0.0') {
            transitive = false
        }
        compile ('com.sapienter.jbilling:database-configurations:1.0.0') {
            transitive = false
        }
        compile ('com.sapienter.jbilling:item-service:1.0.0') {
            transitive = false
        }
        compile ('com.sapienter.jbilling:jbilling-common-impl:1.0.0') {
            transitive = false
        }
        compile ('com.sapienter.jbilling:filter-service:1.0.0') {
            transitive = false
        }
        compile ('com.sapienter.jbilling:jbilling-service:1.0.0') {
            transitive = false
        }
        compile ('com.sapienter.jbilling:mediation-process-service:1.0.0') {
            transitive = false
        }
        compile ('com.sapienter.jbilling:mediation-process-service-impl:1.0.0') {
            transitive = false
        }
        compile ('com.sapienter.jbilling:mediation-service:1.0.0') {
            transitive = false
        }
        compile ('com.sapienter.jbilling:mediation-service-impl:1.0.0') {
            transitive = false
        }
        compile ('com.sapienter.jbilling:order-service:1.0.0') {
            transitive = false
        }
        compile ('com.sapienter.jbilling:event-service:1.0.0') {
            transitive = false
        }
        compile ('com.sapienter.jbilling:usage-pool-service:1.0.0') {
            transitive = false
        }
        test    ('com.sapienter.jbilling:ui-testing-automation:1.0.0') {
            transitive = false
        }
        // ui-testing-automation module dependencies
        test    ("com.googlecode.json-simple:json-simple:1.1.1",
                 "org.uncommons:reportng:1.1.4"
        ) {
            transitive = false
        }
    }

    plugins {
        build ":tomcat:8.0.30"

        compile ":jquery-ui:1.10.4"
        compile ':webflow:2.1.0'
        compile ":cookie:0.51"
        compile ":cxf:2.1.1"
        compile ":remote-pagination:0.4.8"
        compile ":remoting:1.3"
        compile ":spring-security-core:2.0-RC4"

        runtime(":hibernate4:4.3.5.5") {
            excludes "hibernate-validator"
        }
        runtime ":jquery:1.11.1"
        runtime ":resources:1.2.8"
        runtime ":webxml:1.4.1"
    }
}
