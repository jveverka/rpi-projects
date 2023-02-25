#!/usr/bin/python3

import RPi.GPIO as GPIO
import json
import ssl
import sys
import time
import logging
import base64
import http.client

cf = open(sys.argv[1])
config = json.load(cf)

meter_pin = config["meter-pin"]

GPIO.setmode(GPIO.BOARD)
GPIO.setup(meter_pin, GPIO.IN)
GPIO.setup(meter_pin, GPIO.IN, pull_up_down=GPIO.PUD_UP)

logging.basicConfig(filename='powermeter.log', level=logging.INFO)
#logging.basicConfig(format='%(levelname)s: %(message)s', level=logging.INFO)
logging.info('RPi Power Meter 1.0.0')
logging.info('Meter PIN status = %s', GPIO.input(meter_pin))
logging.info('loading config file: %s', sys.argv[1])

device_id    = config["device-id"]
pulse_value  = config["pulse-value"]
voltage_ac   = config["voltage-ac"]
cost_kwh     = config["cost-kwh"]
co2g_per_kwh = config["co2g-per-kwh"]
max_power_kw = config["max-power-kw"]

elastic      = config["data-store"]["elastic"]
index        = config["data-store"]["index"]
elastic_user = config["data-store"]["user"]
elastic_pass = config["data-store"]["password"]

logging.info('device_id: %s', device_id)
logging.info('pulse_value: %s', pulse_value)
logging.info('voltage_ac: %s', voltage_ac)
logging.info('cost_kwh: %s', cost_kwh)
logging.info('elastic: %s', elastic)
logging.info('index: %s', index)
logging.info('co2g_per_kwh: %s', co2g_per_kwh)
logging.info('max-power-kw: %s', max_power_kw)
logging.info('meter_pin: %s', meter_pin)


def get_authorization(username, password):
    auth_str = username + ":" + password
    return "Basic " + base64.b64encode(auth_str.encode('ascii')).decode('ascii')


elastic_authorization = get_authorization(elastic_user, elastic_pass)
last_timestamp = 0
logging.info('elastic_authorization: %s', elastic_authorization)
elastic_headers = {
    "Content-type": "application/json",
    "Authorization": elastic_authorization
}

def write_to_elastic(device_id, now_timestamp, interval, voltage, consumed, price, power, current, co2_produced):
    now_timestamp = int(now_timestamp * 1000)
    body = {
        "timestamp": now_timestamp,
        "deviceId": device_id,
        "interval": interval,
        "voltage": voltage,
        "consumed": consumed,
        "price": price,
        "co2gProduced": co2_produced,
        "calculated": {
            "power": power,
            "current": current
        }
    }
    #connection = http.client.HTTPConnection(elastic)
    connection = http.client.HTTPSConnection(elastic, context = ssl._create_unverified_context())
    connection.request("POST", index + "/_doc", headers = elastic_headers, body = json.dumps(body))
    response = connection.getresponse()
    logging.info("elastic response: %s", response.status)


while True:
    GPIO.wait_for_edge(meter_pin, GPIO.FALLING)
    now_timestamp = time.time()
    power   = 0
    current = 0
    delta_time = 0
    co2_produced = 0
    price = 0
    if (last_timestamp > 0):
        delta_time = now_timestamp - last_timestamp
        power   = 1000 * ((pulse_value*3600) / delta_time)
        current = power / voltage_ac
        co2_produced = co2g_per_kwh * pulse_value
        price = cost_kwh * pulse_value
    local_time = time.localtime(now_timestamp)
    time_str = time.strftime("%Y-%m-%d %H:%M:%S", local_time)
    if ((power / 1000) < max_power_kw):
        last_timestamp = now_timestamp
        logging.info('Meter pulse: ' + time_str + ' [' + str(now_timestamp) + '] | P=' + str(power) + ' W | I=' + str(current) + ' A | delta time: ' + str(delta_time) + ' s | price: ' + str(price) + ' Eur | CO2: ' + str(co2_produced) + 'g')
        write_to_elastic(device_id, now_timestamp, delta_time, voltage_ac, pulse_value, price, power, current, co2_produced)
    else:
        logging.info('ERROR: Meter pulse exceeded MAX power limit ! ' + time_str + ' [' + str(now_timestamp) + '] | MAX P=' + str(max_power_kw) + ' kW | P=' + str(power) + ' W | I=' + str(current) + ' A | delta time: ' + str(delta_time) + ' s')

GPIO.cleanup()
