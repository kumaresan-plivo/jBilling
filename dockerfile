# Pull base image
From tomcat:8-jre8

ENV JBILLING_DB_HOST=192.168.1.37
ENV JBILLING_ACTIVE_MQ_BROKER_URL=tcp://192.168.1.37:61616

# Copy to images tomcat path
ADD target/jbilling.war /usr/local/tomcat/webapps/