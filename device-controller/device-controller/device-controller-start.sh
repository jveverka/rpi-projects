#!/bin/sh

echo "starting device-controller ..."
echo "APP_CONFIG_PATH=${APP_CONFIG_PATH}"
echo "XMX=${XMX}"
sleep 5 # wait for mongo DB to start
java -Xms32m -Xmx${XMX} -Djava.security.egd=file:/dev/./urandom -jar /device-controller.jar \
     --spring.config.location=file:${APP_CONFIG_PATH}
