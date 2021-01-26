[![Maven Central](https://maven-badges.herokuapp.com/maven-central/one.microproject.rpi/rpi-drivers/badge.svg)](https://maven-badges.herokuapp.com/maven-central/one.microproject.rpi/rpi-drivers)

# RPI Drivers

Collection of drivers for various chips for Raspberry PI
Written in Java using [pi4j](http://pi4j.com/).

## Supported chips:
* __BMP180__ - bosch I2C temperature and pressure sensor
* __DS1621__ - I2C temperature sensor
* __HCSR04__ - ultrasonic proximity detector
* __HCSR501__ - ultrasonic proximity detector 
* __HTU21D__ - I2C relative humidity and temperature sensor
* __MCP9808__ - I2C digital temperature sensor

## Use, Compile and Install
* Use official [published artefacts](https://search.maven.org/search?q=one.microproject.rpi), or ...
* Clone this git repository.
* Run ``gradle clean build test publishToMavenLocal`` to install this jar locally.
* Use maven or gradle dependency below.

### use with maven
```
<dependency>
    <groupId>one.microproject.rpi</groupId>
    <artifactId>rpi-drivers</artifactId>
    <version>1.0.0</version>
</dependency>
```

### use with gradle
```
implementation 'one.microproject.rpi:rpi-drivers:1.0.0'
```

### testing
* install java on raspberry pi
* enable I2C on raspberry pi
* copy pi4j and rpi-drivers jars in tmp directory
* wire hardware as necessary
* start tests in one.microproject.rpi.hardware.gpio.tests


