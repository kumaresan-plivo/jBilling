#!/bin/bash
#
# JBILLING CONFIDENTIAL
# _____________________
#
# [2003] - [2012] Enterprise jBilling Software Ltd.
# All Rights Reserved.
#
# NOTICE:  All information contained herein is, and remains
# the property of Enterprise jBilling Software.
# The intellectual and technical concepts contained
# herein are proprietary to Enterprise jBilling Software
# and are protected by trade secret or copyright law.
# Dissemination of this information or reproduction of this material
# is strictly forbidden.

export GRAILS_OPTS="-server -Xmx1536M -Xms256M -XX:MaxPermSize=384m"

mvn compile -f service-modules/pom.xml;grails -noreloading -Ddisable.auto.recompile=true run-app
