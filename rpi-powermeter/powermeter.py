#!/usr/bin/python3

import RPi.GPIO as GPIO
import time
import logging

switch_pin = 15

GPIO.setmode(GPIO.BOARD)
GPIO.setup(switch_pin, GPIO.IN)
GPIO.setup(switch_pin, GPIO.IN, pull_up_down=GPIO.PUD_UP)

print('RPi Power Meter 1.0.0')
print('Meter PIN status = ', GPIO.input(switch_pin))
logging.basicConfig(filename='powermeter.log', level=logging.INFO)

meter_pulse = 0.001 # pulse each 0.001 kWh
ac_voltage  = 230
cost_kwh    = 0.1354436

consumed_power = 0
last_timestamp = 0

while True:
    GPIO.wait_for_edge(switch_pin, GPIO.FALLING)
    now_timestamp = time.time()
    power   = 0
    current = 0
    delta_time = 0
    if (last_timestamp > 0):
        delta_time = now_timestamp - last_timestamp
        power   = 1000 * ((meter_pulse*3600) / delta_time)
        current = power / ac_voltage
    consumed_power = consumed_power + meter_pulse
    local_time = time.localtime(now_timestamp)
    time_str = time.strftime("%Y-%m-%d %H:%M:%S", local_time)
    cost = consumed_power * cost_kwh
    logging.info('Meter pulse: ' + time_str + ' [' + str(now_timestamp) + '] | P=' + str(power) + ' W | I=' + str(current) + ' A | consumed: ' + str(consumed_power) + ' kWh | delta time: ' + str(delta_time) + ' s | cost: ' + str(cost) + ' Eur')
    last_timestamp = now_timestamp

GPIO.cleanup()
