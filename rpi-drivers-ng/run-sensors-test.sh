#!/bin/bash

#wget https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar
#wget https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.36/slf4j-simple-1.7.36.jar
#wget https://repo1.maven.org/maven2/com/pi4j/pi4j-plugin-raspberrypi/2.1.1/pi4j-plugin-raspberrypi-2.1.1.jar
#wget https://repo1.maven.org/maven2/com/pi4j/pi4j-core/2.1.1/pi4j-core-2.1.1.jar
#wget https://repo1.maven.org/maven2/com/pi4j/pi4j-plugin-linuxfs/2.1.1/pi4j-plugin-linuxfs-2.1.1.jar
#wget https://repo1.maven.org/maven2/com/pi4j/pi4j-plugin-pigpio/2.1.1/pi4j-plugin-pigpio-2.1.1.jar

CLASSPATH="slf4j-api-1.7.36.jar:slf4j-simple-1.7.36.jar"
CLASSPATH="${CLASSPATH}:pi4j-core-2.1.1.jar"
CLASSPATH="${CLASSPATH}:pi4j-plugin-raspberrypi-2.1.1.jar"
CLASSPATH="${CLASSPATH}:pi4j-plugin-linuxfs-2.1.1.jar"
CLASSPATH="${CLASSPATH}:pi4j-plugin-pigpio-2.1.1.jar"
CLASSPATH="${CLASSPATH}:rpi-drivers-2.0.0.jar"

java -classpath $CLASSPATH one.microproject.rpi.hardware.gpio.sensors.tests.PCF8591Test
