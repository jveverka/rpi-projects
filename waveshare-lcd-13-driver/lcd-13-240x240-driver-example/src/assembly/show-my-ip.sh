#!/bin/bash

# use 'crontab -e' and add this line to make this run after RPi boots
# @reboot /path/to/app/show-my-ip.sh &

XDIR=`dirname $0`
CLASS_PATH=${XDIR}/lcd-13-240x240-driver-1.0.0.jar
CLASS_PATH=${CLASS_PATH}:${XDIR}/lcd-13-240x240-driver-example.jar

sleep 10
IP_ADDRESS=`hostname -I`
DELAY=20000
echo "${XDIR} ${IP_ADDRESS}"

export LD_LIBRARY_PATH=${XDIR}
java -Djava.library.path=${XDIR} -cp ${CLASS_PATH} itx.raspberry.drivers.waveshare.lcd13_240x240.example.DisplayText ${IP_ADDRESS} ${DELAY}

