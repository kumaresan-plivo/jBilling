import groovy.io.FileType

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

includeTargets << grailsScript("_GrailsDocs")
includeTargets << new File("${basedir}/scripts/SetLicense.groovy")
includeTargets << new File("${basedir}/scripts/CheckDbConnection.groovy")

eventCreateWarStart = { warName, stagingDir ->
    println("Compiling documentation ...")
}

eventCompileEnd = {
    setLicense()
    checkDbConnection()
}

eventStatusUpdate = { msg ->
    if (msg == "Running Grails application") {
        setLicense()
        def extDir = new File("${basedir}/ext")
        if (extDir.exists()) {
            extDir.eachFileRecurse (FileType.FILES) { file ->
                println "Extending classpath with custom classes from ${file}"
                classLoader.addURL(file.toURI().toURL())
            }
        }
    }
}

