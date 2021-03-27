[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![Build and Test](https://github.com/jveverka/rpi-projects/workflows/Build%20and%20Test/badge.svg)
[![Maven Central](https://img.shields.io/badge/maven%20central-release-green.svg)](https://search.maven.org/search?q=one.microproject.rpi)

# Raspberry PI projects and drivers

![banner](docs/graphics-banner.svg)

This project contains various DYI projects, hardware drivers, schematics and code examples for Raspberry PI.

### Hardware drivers 
* __[rpi-drivers](rpi-drivers)__ - Java APIs for some PRi/[pi4j](https://pi4j.com/) compatible hardware.
* __[waveshare-lcd-13-driver](waveshare-lcd-13-driver)__ - Java driver for small LCD display.
* __[sensehat-games](sensehat-games)__ - small compilation of python games for 8x8 [sensehat](https://www.raspberrypi.org/products/sense-hat/) display.

### RPi Power Controller   
* __[rpi-powercontroller](rpi-powercontroller)__ - Control relay power switch via HTTP/REST APIs.
* __[rpi-powercontroller-client](rpi-powercontroller-client)__ - Java Client for [rpi-powercontroller](rpi-powercontroller).

### RPi Camera
* __[rpi-camera](rpi-camera)__ - Capture images via HTTP/REST APIs. 
* __[rpi-camera-client](rpi-camera-client)__ - Java Client for [rpi-camera](rpi-camera).

### Kubernetes Cluster
* __[Kubernetes Cluster](k8s-cluster)__ - running backend on Arm64 RPi4 hardware.

## Build and Test
``
gradle clean build test
``

*Enjoy !*

