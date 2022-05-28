[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![Build and Test](https://github.com/jveverka/rpi-projects/workflows/Build%20and%20Test/badge.svg)
[![Maven Central](https://img.shields.io/badge/maven%20central-release-green.svg)](https://search.maven.org/artifact/one.microproject.rpi/rpi-drivers)

# Raspberry PI projects and drivers

![banner](docs/graphics-banner.svg)

This project contains various DYI projects, hardware drivers, schematics and code examples for Raspberry PI.

### Hardware drivers & docs
* __[rpi-drivers](rpi-drivers)__ - Java APIs for some [pi4j-v2](https://pi4j.com/) I2C compatible hardware.
* __[waveshare-lcd-13-driver](waveshare-lcd-13-driver)__ - Java driver for small LCD display.
* __[sensehat-games](sensehat-games)__ - small compilation of python games for 8x8 [sensehat](https://www.raspberrypi.org/products/sense-hat/) display.
* __[pir-motion-detectors](pir-motion-detectors)__ - RPi compatible motion detectors.

### RPi Power Controller   
* __[rpi-powercontroller](rpi-powercontroller)__ - Control relay power switch via HTTP/REST APIs.
* __[rpi-powercontroller-client](rpi-powercontroller-client)__ - Java Client for [rpi-powercontroller](rpi-powercontroller).

### RPi Power Meters
* __[rpi-powermeter](rpi-powermeter)__ - Single phase AC power consumption meter.
* __[rpi-voltage-current](rpi-voltage-current)__ - DC voltage, current and power metering.

### RPi Camera
* __[rpi-camera](rpi-camera)__ - Capture images via HTTP/REST APIs. 
* __[rpi-camera-client](rpi-camera-client)__ - Java Client for [rpi-camera](rpi-camera).

### RPi Radiation Monitoring
* __[rpi-radiation-monitor](rpi-radiation-monitor)__ - simple radiation background monitor with HTTP/REST access.

### Others
* __[Device Controller](device-controller)__ - controller gateway for RPi devices.
* __[Kubernetes Cluster](k8s-cluster)__ - running backend on Arm64 RPi4 hardware.
* __[Guidelines and Procedures](guidelines-and-procedures)__ - various guidelines.

## Build and Test
``
gradle clean build test
``

*Enjoy !*

