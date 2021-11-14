#!/usr/bin/python3

import RPi.GPIO as GPIO
import json
import sys
import time
import logging
import http.client

switch_pin = 15

GPIO.setmode(GPIO.BOARD)
GPIO.setup(switch_pin, GPIO.IN)
GPIO.setup(switch_pin, GPIO.IN, pull_up_down=GPIO.PUD_UP)

logging.basicConfig(filename='powermeter.log', level=logging.INFO)
#logging.basicConfig(format='%(levelname)s: %(message)s', level=logging.INFO)
logging.info('RPi Power Meter 1.0.0')
logging.info('Meter PIN status = %s', GPIO.input(switch_pin))
logging.info('loading config file: %s', sys.argv[1])

cf = open(sys.argv[1])
config = json.load(cf)

device_id   = config["device-id"]
pulse_value = config["pulse-value"]
voltage_ac  = config["voltage-ac"]
cost_kwh    = config["cost-kwh"]
elastic     = config["data-store"]["elastic"]
index       = config["data-store"]["index"]

logging.info('device_id: %s', device_id)
logging.info('pulse_value: %s', pulse_value)
logging.info('voltage_ac: %s', voltage_ac)
logging.info('cost_kwh: %s', cost_kwh)
logging.info('elastic: %s', elastic)
logging.info('index: %s', index)

consumed_power = 0
last_timestamp = 0

def write_to_elastic(device_id, now_timestamp, interval, voltage, consumed, price, power, current):
    now_timestamp = int(now_timestamp * 1000)
    headers = {"Content-type": "application/json"}
    body = {
        "timestamp": now_timestamp,
        "deviceId": device_id,
        "interval": interval,
        "voltage": voltage,
        "consumed": consumed,
        "price": price,
        "calculated": {
            "power": power,
            "current": current
        }
    }
    connection = http.client.HTTPConnection(elastic)
    connection.request("POST", index + "/_doc", headers = headers, body = json.dumps(body))
    response = connection.getresponse()
    logging.info("elastic response: %s", response.status)

while True:
    GPIO.wait_for_edge(switch_pin, GPIO.FALLING)
    now_timestamp = time.time()
    power   = 0
    current = 0
    delta_time = 0
    if (last_timestamp > 0):
        delta_time = now_timestamp - last_timestamp
        power   = 1000 * ((pulse_value*3600) / delta_time)
        current = power / voltage_ac
    consumed_power = consumed_power + pulse_value
    local_time = time.localtime(now_timestamp)
    time_str = time.strftime("%Y-%m-%d %H:%M:%S", local_time)
    cost = consumed_power * cost_kwh
    logging.info('Meter pulse: ' + time_str + ' [' + str(now_timestamp) + '] | P=' + str(power) + ' W | I=' + str(current) + ' A | consumed: ' + str(consumed_power) + ' kWh | delta time: ' + str(delta_time) + ' s | cost: ' + str(cost) + ' Eur')
    write_to_elastic(device_id, now_timestamp, delta_time, voltage_ac, pulse_value, cost_kwh, power, current)
    last_timestamp = now_timestamp

GPIO.cleanup()
