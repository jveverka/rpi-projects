#!/usr/bin/env python3

import smbus
import time

address = 0x48
A0 = 0x40
A1 = 0x41

bus = smbus.SMBus(1)

print("{:>5}\t{:>5}".format('raw', 'voltage'))

while True:
    bus.write_byte(address,A0)
    value0 = bus.read_byte(address)
    voltage0 = ( 3.3 / 255 ) * value0

    bus.write_byte(address,A1)
    value1 = bus.read_byte(address)
    voltage1 = ( 3.3 / 255 ) * value1

    print("CH0: {:>5}\t{:>5.3f} V".format(value0, voltage0))
    print("CH1: {:>5}\t{:>5.3f} V".format(value1, voltage1))

    time.sleep(0.5)
