#!/usr/bin/env python3

import time
import board
import busio
import adafruit_ads1x15.ads1115 as ADS
from adafruit_ads1x15.analog_in import AnalogIn

# Create the I2C bus
i2c = busio.I2C(board.SCL, board.SDA)

# Create the ADC object using the I2C bus
ads = ADS.ADS1115(i2c)

# Create single-ended input on channel 0 and channel 2
chan0 = AnalogIn(ads, ADS.P0)
chan1 = AnalogIn(ads, ADS.P1)

# Create differential input between channel 0 and 1
#chan = AnalogIn(ads, ADS.P0, ADS.P1)

print("{:>5}\t{:>5}".format('raw', 'voltage'))

while True:
    print("CH0: {:>5}\t{:>5.3f} V".format(chan0.value, chan0.voltage))
    print("CH1: {:>5}\t{:>5.3f} V".format(chan1.value, chan1.voltage))
    time.sleep(0.5)
