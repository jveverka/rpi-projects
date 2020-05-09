[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://travis-ci.org/jveverka/itx-rpi-drivers.svg?branch=master)](https://travis-ci.org/jveverka/itx-rpi-drivers)

# Raspberry PI hardware drivers

![banner](docs/graphics-banner.svg)

This project contains various hardware drivers, schematics and code examples for Raspberry PI.

* __[pi4j-drivers](pi4j-drivers)__ - Java APIs for some PRi compatible hardware.
* __[rpi-powercontroller](rpi-powercontroller)__ - Control relay power switch via HTTP/REST APIs. 
* __[rpi-camera](rpi-camera)__ - Capture images via HTTP/REST APIs. 
* __[waveshare-lcd-13-driver](waveshare-lcd-13-driver)__ - Java driver for small LCD display.
* __[sensehat-games](sensehat-games)__ - Simple compilation of python games for 8x8 [sensehat](https://www.raspberrypi.org/products/sense-hat/) display.

## Build and Test
``
gradle clean build test
``

*Enjoy !*

