#!/usr/bin/env python3

import io
import sys
import time
import json
import base64
import picamera
import logging
import socketserver
from threading import Condition
from http import server
import RPi.GPIO as GPIO


#------------------------------------------------------------------------------
# INIT SECTION - BEGIN
#------------------------------------------------------------------------------
logging.basicConfig(format='%(levelname)s:%(message)s', level=logging.INFO)

logging.info('RPi Camera REST')
logging.info('app initialization started')

started = int(time.time())
cf = open(sys.argv[1])
config = json.load(cf)
defaults = config['defaults']

camera_rotations = {
  "D0": 0,
  "D90": 90,
  "D180": 180,
  "D270": 270
}

logging.info('app initialization done.')
#------------------------------------------------------------------------------
# INIT SECTION - DONE
#------------------------------------------------------------------------------


class StreamingOutput(object):
    def __init__(self):
        self.frame = None
        self.buffer = io.BytesIO()
        self.condition = Condition()

    def write(self, buf):
        if buf.startswith(b'\xff\xd8'):
            # New frame, copy the existing buffer's content and notify all
            # clients it's available
            self.buffer.truncate()
            with self.condition:
                self.frame = self.buffer.getvalue()
                self.condition.notify_all()
            self.buffer.seek(0)
        return self.buffer.write(buf)

class StreamingHandler(server.BaseHTTPRequestHandler):
    def do_GET(self):
        authorization = self.headers['authorization']
        logging.debug("authorization: %s", str(authorization))
        if not isAuthorized(authorization, config['credentials']):
            self.send_response(401)
            self.send_header('WWW-Authenticate', 'Basic realm="Access to RPi camera"')
            self.send_header('Proxy-Authenticate', 'Basic realm="Access to RPi camera"')
            self.end_headers()
        elif self.path == '/system/stream.mjpg':
            self.send_response(200)
            self.send_header('Age', 0)
            self.send_header('Cache-Control', 'no-cache, private')
            self.send_header('Pragma', 'no-cache')
            self.send_header('Content-Type', 'multipart/x-mixed-replace; boundary=FRAME')
            self.end_headers()
            try:
                while True:
                    with output.condition:
                        output.condition.wait()
                        frame = output.frame
                    self.wfile.write(b'--FRAME\r\n')
                    self.send_header('Content-Type', 'image/jpeg')
                    self.send_header('Content-Length', len(frame))
                    self.end_headers()
                    self.wfile.write(frame)
                    self.wfile.write(b'\r\n')
            except Exception as e:
                logging.warning(
                    'Removed streaming client %s: %s',
                    self.client_address, str(e))
        elif self.path == '/system/info':
            uptime = int(time.time()) - started
            version = { "id": config['id'], "type": "camera-rest", "version": "1.6.0", "name": config['name'], "timestamp": int(time.time()), "uptime": uptime, "properties": { "revision": camera.revision } }
            content = json.dumps(version).encode('utf-8')
            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            self.send_header('Content-Length', len(content))
            self.end_headers()
            self.wfile.write(content)
        elif self.path == '/system/capture':
            self.send_response(200)
            with output.condition:
                 output.condition.wait()
                 frame = output.frame
            self.send_header('Content-Type', 'image/jpeg')
            self.send_header('Content-Length', len(frame))
            self.end_headers()
            self.wfile.write(frame)
            self.wfile.write(b'\r\n')
        else:
            self.send_error(404)
            self.end_headers()

class StreamingServer(socketserver.ThreadingMixIn, server.HTTPServer):
    allow_reuse_address = True
    daemon_threads = True

def isAuthorized(authorization, credentials):
    if authorization == None:
       logging.error("authorization header is missing !"); 
       return False
    if not authorization.startswith( 'Basic ' ):   
       logging.error("authorization Basic is expected !"); 
       return False
    decoded = str(base64.b64decode(authorization.split()[1]),'utf-8')
    logging.info("decoded %s", decoded);
    auth_split = decoded.split(":")
    if auth_split[0] in credentials:
       password = credentials[auth_split[0]]
       if password == auth_split[1]:
          logging.info("user and password match !");
          return True   
    logging.error("authorization failed !");
    return False

with picamera.PiCamera(resolution='640x480', framerate=24) as camera:
    output = StreamingOutput()
    camera.rotation = camera_rotations[defaults['rotation']]
    camera.start_recording(output, format='mjpeg')
    try:
        address = (config['host'], config['port'])
        logging.info("starting server at: %s", str(address))
        server = StreamingServer(address, StreamingHandler)
        server.serve_forever()
    finally:
        logging.info("app stopped")
        camera.stop_recording()
        sys.exit(0)    
        pass

