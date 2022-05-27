#!/usr/bin/python3

import RPi.GPIO as GPIO
import json
import sys
import time
import logging
import base64
import threading
from http.server import BaseHTTPRequestHandler, HTTPServer

monitor_pin=15
tube_constant=151
hostname='0.0.0.0'
port=8080
interval=60
measurements = []
radiation = 0
cpm = 0
counter= 0
uptime = 0
started= 0

GPIO.setmode(GPIO.BOARD)
GPIO.setup(monitor_pin, GPIO.IN)
GPIO.setup(monitor_pin, GPIO.IN, pull_up_down=GPIO.PUD_UP)

logging.basicConfig(format='%(levelname)s: %(message)s', level=logging.INFO)
logging.info('RPi Radiation Monitor 1.0.0')

class ServerHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.send_header("Content-type", "application/json")
        self.end_headers()
        timestamp = time.time()
        response ={
            "radiation": {
                "value": radiation,
                "unit": "uSv/h"
            },
            "counter": {
                "cpm": cpm,
                "total": counter
            },
            "timestamp": timestamp,
            "uptime": uptime
        }
        responseBody = json.dumps(response);
        self.wfile.write(bytes(responseBody, "utf-8"))

def start_web_server(hostname, port):
    webServer = HTTPServer((hostname, port), ServerHandler)
    logging.info('Web server started !');
    webServer.serve_forever()
    return webServer

def add_and_calculate(timestamp, measurements, interval):
    evictiontime = timestamp - (interval * 1000)
    measurements_copy = []
    for ts in measurements:
        if ts > evictiontime:
            measurements_copy.append(ts)
    measurements_copy.append(timestamp)
    cpm = len(measurements_copy)
    radiation = cpm/tube_constant
    return measurements_copy, cpm, radiation

webThread = threading.Thread( target=start_web_server, args=(hostname, port) )
webThread.start()
started = time.time()

while True:
    timestamp = time.time() * 1000
    uptime = time.time() - started
    GPIO.wait_for_edge(monitor_pin, GPIO.FALLING)
    counter = counter + 1
    measurements, cpm, radiation = add_and_calculate(timestamp, measurements, interval)
    logging.info('CPM: ' + str(cpm) + ', radiation: ' + str(radiation) + ' uSv/h')

webThread.stop()
GPIO.cleanup()
