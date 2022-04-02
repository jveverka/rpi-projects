# RPI Voltage and Current sensors
This projects shows how to use some of AD converters and current sensors with raspberry pi.
![reference](docs/rpi-voltage-current-reference.drawio.svg)

## General setup
1. Enable I2C bus.
   ``` 
   sudo raspi-config
   ```
2. Install gpio and i2c packages.   
   ```
   sudo apt-get install python3-smbus python3-dev i2c-tools python3-pip
   pip3 install adafruit-circuitpython-ads1x15
   sudo i2cdetect -y 1
   ```
   
### PCF8591T 4CH 8-Bit A/D Converter
* [Datasheet](docs/PCF8591.pdf) 
* [Sample code](pcf8591t.py)

![PCF8591](docs/PCF8591.png)

### ADS1115 4CH 16-Bit A/D Converter
* [Datasheet](docs/ads1115.pdf)
* [Library](https://github.com/adafruit/Adafruit_Python_ADS1x15)
* [Sample Code](ads1115.py)
 
![ADS1115](docs/ads1115.jpg)