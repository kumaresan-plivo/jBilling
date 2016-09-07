docker run -it --rm \
-e "JBILLING_SERVER_URL=http://localhost:8080/jbilling" \
-e "JBILLING_DB_HOST=192.168.1.37" \
-e "JBILLING_ACTIVE_MQ_BROKER_URL=tcp://192.168.1.37:61616" \
-e "JBILLING_HOME=/opt/jbilling_home/" \
-v ~/jbilling_home_batch/:/opt/jbilling_home/ \
--cpuset-cpus=2,3 --memory="4096M" --name jbilling-batch-1 jbilling-batch
