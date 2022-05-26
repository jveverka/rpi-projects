#!/usr/bin/python3

import RPi.GPIO as GPIO
import json
import sys
import time
import logging
import base64
import threading

GPIO.setmode(GPIO.BOARD)
GPIO.setup(15, GPIO.IN)
GPIO.setup(15, GPIO.IN, pull_up_down=GPIO.PUD_UP)

logging.basicConfig(format='%(levelname)s: %(message)s', level=logging.INFO)
logging.info('RPi Radiation Meter 1.0.0')

interval=60
measurements = []

def add_and_calculate(timestamp, measurements, interval):
    evictiontime = timestamp - (interval * 1000)
    measurements_copy = []
    for ts in measurements:
        if ts > evictiontime:
            measurements_copy.append(ts)
    measurements_copy.append(timestamp)
    cpm = len(measurements_copy)
    radiation = cpm/151
    return measurements_copy, cpm, radiation

while True:
    timestamp = time.time() * 1000
    GPIO.wait_for_edge(15, GPIO.FALLING)
    measurements, cpm, radiation = add_and_calculate(timestamp, measurements, interval)
    logging.info('CPM: ' + str(cpm) + ', radiation: ' + str(radiation) + ' uSv/h')

GPIO.cleanup()