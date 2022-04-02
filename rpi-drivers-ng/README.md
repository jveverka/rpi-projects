[![Maven Central](https://maven-badges.herokuapp.com/maven-central/one.microproject.rpi/rpi-drivers/badge.svg)](https://maven-badges.herokuapp.com/maven-central/one.microproject.rpi/rpi-drivers)

# RPI Drivers
Collection of drivers for various I2C chips for Raspberry PI
Written in Java using [pi4j](http://pi4j.com/).
RaspberryPi compatibility: 3, 4, zero

## Supported chips:
* __BMP180__ - bosch I2C temperature and pressure sensor
* __HTU21D__ - I2C relative humidity and temperature sensor
* __PCF8591__ - I2C 8-bit 4-channel ADC
* __ADS1115__ - I2C 16-bit 4-channel ADC

### Use with maven
```
<dependency>
    <groupId>one.microproject.rpi</groupId>
    <artifactId>rpi-drivers</artifactId>
    <version>2.0.0</version>
</dependency>
```

### Use with gradle
```
implementation 'one.microproject.rpi:rpi-drivers:2.0.0'
```

## Compile and Test on RaspberryPI
1. Install java on raspberry pi.
   ```
   sudo apt install openjdk-11-jdk
   ```
2. Enable I2C on raspberry pi using ``sudo raspi-config``.
3. Make sure all sensors are connected to RaspberryPI device.
   ```
   i2cdetect -y 1
   ```
   Check reference test schema for I2C sensors.
4. Build driver library locally (on PC):
   ```
   gradle clean build test publishToMavenLocal installDist distZip
   ```
5. Copy to RaspberryPI device:
   ```
   scp build/distributions/rpi-drivers-ng-2.0.0.zip pi@<raspberry-pi>:/home/pi/
   ```
6. Run tests on RaspberryPI device:
   ```
   unzip rpi-drivers-ng-2.0.0.zip
   cd rpi-drivers-ng-2.0.0/bin
   ./rpi-drivers-ng ALL, ADS1115, BMP180, HTU21D, PCF8591 
   ```
