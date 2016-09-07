docker run -it --rm -p 8080:8080 \
-e "JBILLING_SERVER_URL=http://localhost:8080/jbilling" \
-e "JBILLING_DB_HOST=192.168.1.37" \
-e "JBILLING_ACTIVE_MQ_BROKER_URL=tcp://192.168.1.37:61616" \
-e "JBILLING_HOME=/opt/jbilling_home/" \
-v ~/jbilling_home_master/:/opt/jbilling_home/ \
--cpuset-cpus=0,1 --memory="4096M" --name jbilling-ui jbilling-ui
