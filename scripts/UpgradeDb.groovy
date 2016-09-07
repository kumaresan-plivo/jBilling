/*
 * JBILLING CONFIDENTIAL
 * _____________________
 *
 * [2003] - [2012] Enterprise jBilling Software Ltd.
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

includeTargets << grailsScript("Init")
includeTargets << new File("${basedir}/scripts/Liquibase.groovy")

target(upgradeDb: "Upgrades database to the latest version") {
    depends(parseArguments, initLiquibase)

    def version = getApplicationMinorVersion(argsMap)

    println "Upgrading database to version ${version}"
    echoDatabaseArgs()

    // changelog files to load
    def upgrade = "descriptors/database/jbilling-upgrade-${version}.xml"

    // run the upgrade scripts
    // by default this will run the upgrade context
    // if the -test argument is given then the test data will be updated
    // if the -client argument is given then the client data will be updated
    def context = "base"
    if (argsMap.test) {
        context = "test"
    } else if (argsMap.client) {
        context = "client"
    } else if (argsMap.demo) {
        context = "demo"
    } else if (argsMap.post_base) {
        context = "post_base"
    }

    println "updating with context = $context"
    ant.updateDatabase(liquibaseTaskAttrs(changeLogFile: upgrade, contexts: context))
}

setDefaultTarget(upgradeDb)
