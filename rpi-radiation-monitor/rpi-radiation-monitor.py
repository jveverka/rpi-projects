#!/usr/bin/python3

import RPi.GPIO as GPIO
import json
import sys
import time
import logging
import base64
import threading
from http.server import BaseHTTPRequestHandler, HTTPServer

GPIO.setmode(GPIO.BOARD)
GPIO.setup(15, GPIO.IN)
GPIO.setup(15, GPIO.IN, pull_up_down=GPIO.PUD_UP)

logging.basicConfig(format='%(levelname)s: %(message)s', level=logging.INFO)
logging.info('RPi Radiation Monitor 1.0.0')

interval=60
measurements = []
radiation = 0
cpm = 0
uptime = 0
started= 0

class ServerHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.send_header("Content-type", "application/json")
        self.end_headers()
        timestamp = time.time()
        self.wfile.write(bytes("{ \"radiation\": " + str(radiation) + ", \"cpm\": " + str(cpm) + ", \"unit\": \"uSv/h\", \"timestamp\": " + str(timestamp) + ", \"uptime\": " + str(uptime) + " }", "utf-8"))

def start_web_server():
    webServer = HTTPServer(('0.0.0.0', 8080), ServerHandler)
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
    radiation = cpm/151
    return measurements_copy, cpm, radiation

#webServer = start_web_server()
webThread = threading.Thread( target=start_web_server, args=() )
webThread.start()
started = time.time()

while True:
    timestamp = time.time() * 1000
    uptime = time.time() - started
    GPIO.wait_for_edge(15, GPIO.FALLING)
    measurements, cpm, radiation = add_and_calculate(timestamp, measurements, interval)
    logging.info('CPM: ' + str(cpm) + ', radiation: ' + str(radiation) + ' uSv/h')

webThread.stop()
GPIO.cleanup()
