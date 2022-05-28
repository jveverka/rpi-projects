#!/usr/bin/python3

import RPi.GPIO as GPIO
import json
import sys
import time
import logging
import base64
import threading
from http.server import BaseHTTPRequestHandler, HTTPServer

cf = open(sys.argv[1])
config = json.load(cf)

version = "1.0.0"
tube_constant=151
monitor_pin  = config["monitor-pin"]
hostname = config["host"]
port     = config["port"]

interval = 60
measurements = []
radiation = 0
cpm = 0
counter= 0
uptime = 0
started= 0
timestamp = time.time() * 1000

GPIO.setmode(GPIO.BOARD)
GPIO.setup(monitor_pin, GPIO.IN)
GPIO.setup(monitor_pin, GPIO.IN, pull_up_down=GPIO.PUD_UP)

logging.basicConfig(filename='rpi-radiation-monitor.log', level=logging.INFO)
#logging.basicConfig(format='%(levelname)s: %(message)s', level=logging.INFO)
logging.info('RPi Radiation Monitor %s', version)

class ServerHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        authorization = self.headers['authorization']
        if not is_authorized(authorization, config['credentials']):
           self.send_response(401)
           self.send_header('WWW-Authenticate', 'Basic realm="Access to RPi camera"')
           self.send_header('Proxy-Authenticate', 'Basic realm="Access to RPi camera"')
           self.end_headers()
        elif self.path == '/api/v1/system/info':
           system_info = {
               "id": config['id'],
               "type": "rpi-radiation-monitor",
               "version": version,
               "name": config['name'],
               "timestamp": int(time.time()),
               "uptime": int(uptime)
           }
           self.send_response(200)
           self.send_header("Content-type", "application/json")
           self.end_headers()
           response_body = json.dumps(system_info);
           self.wfile.write(bytes(response_body, "utf-8"))
        elif self.path == '/api/v1/system/measurements':
           self.send_response(200)
           self.send_header("Content-type", "application/json")
           self.end_headers()
           response = {
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
           response_body = json.dumps(response);
           self.wfile.write(bytes(response_body, "utf-8"))
        else:
           self.send_error(404)
           self.end_headers()

def is_authorized(authorization, credentials):
    if authorization is None:
        logging.error("authorization header is missing !")
        return False
    if not authorization.startswith( 'Basic ' ):
        logging.error("authorization Basic is expected !")
        return False
    decoded = str(base64.b64decode(authorization.split()[1]),'utf-8')
    auth_split = decoded.split(":")
    if auth_split[0] in credentials:
        password = credentials[auth_split[0]]
        if password == auth_split[1]:
            logging.info("user and password match !")
            return True
    logging.error("authorization failed !")
    return False

def start_web_server(hostname, port):
    web_server = HTTPServer((hostname, port), ServerHandler)
    logging.info('Web server started %s:%s!', hostname, port);
    web_server.serve_forever()
    return web_server

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
    logging.info('Counter: %s, CPM: %s, radiation: %s uSv/h', counter, cpm, radiation)

webThread.stop()
GPIO.cleanup()
