RPI Drivers
===========

Collection of drivers for various chips for Raspberry PI
Written in Java using pi4j.

supported chips:
----------------
* BMP180 - bosch I2C temperature and pressure sensor
* DS1621 - I2C temperature sensor
* HCSR04 - ultrasonic proximity detector
* HCSR501 - ultrasonic proximity detector 
* HTU21D - I2C relative humidity and temperature sensor
* MCP9808 - I2C digital temperature sensor

how to use this:
----------------
* git clone this repository
* use 'gradle clean install' to install this jar locally
* than use maven or gradle dependency

maven:
------
<dependency>
    <groupId>itx.rpi.hardware</groupId>
    <artifactId>itx-rpi-drivers</artifactId>
    <version>1.0.0</version>
</dependency>

gradle:
-------
compile 'itx.rpi.hardware:itx-rpi-drivers:1.0.0'

testing:
--------
* install java on raspberry pi
* enable I2C on raspberry pi
* copy pi4j and itx-rpi-drivers jars in tmp directory
* wire hardware as necessary
* start tests in itx.rpi.hardware.gpio.tests


