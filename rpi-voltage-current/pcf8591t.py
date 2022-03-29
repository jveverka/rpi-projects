#!/usr/bin/env python3

import smbus
import time

address = 0x48
A0 = 0x40

bus = smbus.SMBus(1)

while True:
    bus.write_byte(address,A0)
    value = bus.read_byte(address)
    voltage = ( 3.3 / 255 ) * value
    print("raw: " + str(value) + " voltage: " + str(round(voltage,3)) + " V")
    time.sleep(0.5)
