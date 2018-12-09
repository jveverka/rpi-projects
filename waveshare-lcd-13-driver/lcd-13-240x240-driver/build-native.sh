#!/bin/bash

mkdir build
echo "BUILD: preparing JNI header files ..."
javah -d src/main/native -classpath src/main/java itx.raspberry.drivers.waveshare.lcd13_240x240.NativeDriverLowLevel
echo "BUILD: compiling display drivers ..."
make -C ../lcd-13-240x240-wiringpi buildlib
echo "BUILD: building JNI library ..."
gcc -I"/usr/lib/jvm/java-8-openjdk-armhf/include" \
    -I"/usr/lib/jvm/java-8-openjdk-armhf/include/linux" \
    -I"../lcd-13-240x240-wiringpi/obj" \
    -I"src/main/native" \
    -L"../lcd-13-240x240-wiringpi/bin" \
    -lLcd13Inch -lc -shared -o build/libWsLcd13NativeDriver.so \
    src/main/native/itx_raspberry_drivers_waveshare_lcd13_240x240_NativeDriverLowLevel.c

