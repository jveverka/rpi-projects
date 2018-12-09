#!/bin/bash

mkdir -p build/application
cp build/libs/lcd*.jar build/application/
cp ../lcd-13-240x240-driver/build/libs/lcd*.jar build/application/
cp src/assembly/*.sh build/application/
chmod 755 build/application/*.sh
cp ../lcd-13-240x240-wiringpi/bin/libLcd13Inch.so build/application/
cp ../lcd-13-240x240-driver/build/libWsLcd13NativeDriver.so build/application/
