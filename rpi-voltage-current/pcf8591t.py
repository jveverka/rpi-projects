#!/usr/bin/env python3

import smbus
import time

address = 0x48
A0 = 0x40
A1 = 0x41

bus = smbus.SMBus(1)

while True:
    bus.write_byte(address,A0)
    value0 = bus.read_byte(address)
    voltage0 = ( 3.3 / 255 ) * value0

    bus.write_byte(address,A1)
    value1 = bus.read_byte(address)
    voltage1 = ( 3.3 / 255 ) * value1

    print("raw: " + str(value0) + " voltage: " + str(round(voltage0,3)) + " V")
    print("raw: " + str(value1) + " voltage: " + str(round(voltage1,3)) + " V")

    time.sleep(0.5)
