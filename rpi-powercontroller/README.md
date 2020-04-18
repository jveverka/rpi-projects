# RPi Power Controller

## REST Endpoints
* __GET__ ``/system/info``
* __GET__ ``/system/measurements``
* __GET__ ``/system/state``
* __PUT__ ``/system/port``
  ``
  { "port": 0, "state": true }
  ``
* __GET__ ``/system/jobs``
* __GET__ ``/system/tasks``

## Build & Run
```
gradle clean build test installDist distZip
./build/install/rpi-powercontroller/bin/rpi-powercontroller
``` 

## Raspberry PI installation
1. Install [Raspbian Buster Lite](https://downloads.raspberrypi.org/raspbian_lite_latest)  
2. Enable I2C bus ``sudo raspi-config`` 
3. Install i2c tools and [wiringpi](http://wiringpi.com/download-and-install/)  
   ``
   sudo apt-get install -y python-smbus i2c-tools
   sudo apt-get install wiringpi
   `` 
4. Check gpio and connected I2C devices
   ```
   gpio -v
   i2cdetect -y 1
   ```
5. Install java
   * Raspberry PI zero [32bit Oracle JRE 8 for ARM](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html)
   * Raspberry 3 or later ``apt install default-jdk``
   