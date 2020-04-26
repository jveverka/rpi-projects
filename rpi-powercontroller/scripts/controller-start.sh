#!/bin/bash

export JAVA_HOME=/opt/rpi-powercontroller/jdk1.8.0_251
export PATH=$JAAVA_HOME/bin:$PATH

LOG_FILE=/opt/rpi-powercontroller/rpi-powercontroller.log
CONFIG=/opt/rpi-powercontroller/rpi-configuration.json

mv $LOG_FILE $LOG_FILE.old

/opt/rpi-powercontroller/rpi-powercontroller-1.0.0/bin/rpi-powercontroller $CONFIG > $LOG_FILE 2>&1

