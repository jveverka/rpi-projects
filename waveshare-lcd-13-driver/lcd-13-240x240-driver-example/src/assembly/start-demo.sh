#!/bin/bash

CLASS_PATH=lcd-13-240x240-driver-1.0.0.jar
CLASS_PATH=${CLASS_PATH}:lcd-13-240x240-driver-example.jar

export LD_LIBRARY_PATH=.
java -Djava.library.path=. -cp ${CLASS_PATH} itx.raspberry.drivers.waveshare.lcd13_240x240.example.NativeDriverExample
