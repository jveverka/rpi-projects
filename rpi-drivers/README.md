[![Maven Central](https://maven-badges.herokuapp.com/maven-central/one.microproject.rpi/rpi-drivers/badge.svg)](https://maven-badges.herokuapp.com/maven-central/one.microproject.rpi/rpi-drivers)

# RPI Drivers
Collection of drivers for various I2C chips for Raspberry PI
Written in Java using [pi4j](http://pi4j.com/).
RaspberryPi compatibility: 3, 4, zero

## Supported chips:
* [__BMP180__](https://www.adafruit.com/product/1603) - Bosch I2C temperature and pressure sensor [datasheet](docs/BMP180.pdf).
* [__BME280__](https://www.adafruit.com/product/2652) - Bosch I2C temperature, humidity and pressure sensor [datasheet](docs/BME280.pdf).
* [__HTU21D__](https://www.adafruit.com/product/3515) - I2C relative humidity and temperature sensor [datasheet](docs/HTU21D.pdf).
* [__PCF8591__](https://www.adafruit.com/product/4648) - I2C 8-bit 4-channel ADC [datasheet](docs/PCF8591.pdf).
* [__ADS1115__](https://www.adafruit.com/product/1085) - I2C 16-bit 4-channel ADC [datasheet](docs/ADS1115.pdf).
* [__BH1750__](https://www.adafruit.com/product/4681) - Light Intensity Sensor [datasheet](docs/BH1750FVI.pdf).

### Use with maven
```
<dependency>
    <groupId>one.microproject.rpi</groupId>
    <artifactId>rpi-drivers</artifactId>
    <version>2.1.1</version>
</dependency>
```

### Use with gradle
```
implementation 'one.microproject.rpi:rpi-drivers:2.1.1'
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
   scp build/distributions/rpi-drivers-2.1.1.zip pi@<raspberry-pi>:/home/pi/
   ```
6. Run tests on RaspberryPI device:
   ```
   unzip rpi-drivers-2.1.1.zip
   cd rpi-drivers-2.1.1/bin
   ./rpi-drivers ALL | ADS1115 | BME280 | BMP180 | HTU21D | PCF8591 | BH1750
   ```

