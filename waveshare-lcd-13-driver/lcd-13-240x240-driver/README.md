# LCD JNI driver library

This is JNI wrapper library for waveshare 240x240 1.3" IPS LCD display.

## Build 
This library must be build after native driver in C has been compiled.
See parent project for build instructions.

## Build results
* __build/libs/lcd-13-240x240-driver-1.0.0.jar__ - JNI interfaces for LCD display
* __build/libWsLcd13NativeDriver.so__ - native shared library for JNI interfaces

### use in java project
##### Maven:
```
<dependency>
    <groupId>itx.raspberry.drivers.waveshare</groupId>
    <artifactId>lcd-13-240x240-driver</artifactId>
    <version>1.0.0</version>
</dependency>
```
##### Gradle:
```
compile 'itx.raspberry.drivers.waveshare:lcd-13-240x240-driver:1.0.0'
```
##### JNI setup:
Before running, do not forget to setup LD_LIBRARY_PATH and -Djava.library.path properly.
Both native libraries libLcd13Inch.so and libWsLcd13NativeDriver.so are available.
