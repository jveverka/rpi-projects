#!/bin/bash

export JAVA_HOME=/opt/rpi-powercontroller/jdk1.8.0_251
export PATH=$JAAVA_HOME/bin:$PATH

CONFIG=/opt/rpi-powercontroller/configuration.json

/opt/rpi-powercontroller/rpi-powercontroller-1.0.0/bin/rpi-powercontroller $CONFIG
