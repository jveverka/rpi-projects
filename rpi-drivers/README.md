[![Maven Central](https://maven-badges.herokuapp.com/maven-central/one.microproject.rpi/rpi-drivers/badge.svg)](https://maven-badges.herokuapp.com/maven-central/one.microproject.rpi/rpi-drivers)

# RPI Drivers
Collection of drivers for various I2C chips for Raspberry PI
Written in Java using [pi4j](http://pi4j.com/).
RaspberryPi compatibility: 3, 4, zero

## Supported chips:
* [__BMP180__](https://www.adafruit.com/product/1603) - Bosch I2C temperature and pressure sensor
* [__BME280__](https://www.adafruit.com/product/2652) - Bosch I2C temperature, humidity and pressure sensor
* [__HTU21D__](https://www.adafruit.com/product/3515) - I2C relative humidity and temperature sensor
* [__PCF8591__](https://www.adafruit.com/product/4648) - I2C 8-bit 4-channel ADC
* [__ADS1115__](https://www.adafruit.com/product/1085) - I2C 16-bit 4-channel ADC

### Use with maven
```
<dependency>
    <groupId>one.microproject.rpi</groupId>
    <artifactId>rpi-drivers</artifactId>
    <version>2.1.0</version>
</dependency>
```

### Use with gradle
```
implementation 'one.microproject.rpi:rpi-drivers:2.1.0'
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
   Check reference documentation for I2C sensors.
4. Build driver library locally (on PC):
   ```
   gradle clean build test publishToMavenLocal installDist distZip
   ```
5. Copy to RaspberryPI device:
   ```
   scp build/distributions/rpi-drivers-2.1.0.zip pi@<raspberry-pi>:/home/pi/
   ```
6. Run tests on RaspberryPI device:
   ```
   unzip rpi-drivers-2.1.0.zip
   cd rpi-drivers-2.1.0/bin
   ./rpi-drivers ALL | ADS1115 | BMP180 | BME280 | HTU21D | PCF8591 
   ```

