#!/usr/bin/env python3

import picamera
import json
import sys 
from flask import Flask, request, jsonify, send_file

camera = picamera.PiCamera()
app = Flask(__name__)
cf = open(sys.argv[1])
config = json.load(cf)

@app.route('/info', methods=["GET"])
def getVersion():
    version = { "id": config['id'], "type": "camera-rest", "version": "1.0.0", "name": config['name'] }
    return jsonify(version)

@app.route('/capture', methods=["GET"])
def capture():
    print('capture')
    camera.capture(config['captureFile'])
    return send_file(config['captureFile'], as_attachment=True)

if __name__ == '__main__':
   try:
     print('camera-rest init.')
     print('#CONFIG Id: ' + config['id'])
     print('#CONFIG captureFile: ' + config['captureFile'])
     camera.resolution = (2592, 1944)
     camera.framerate = 15
     print('camera-rest starting preview')
     camera.start_preview()
     print('camera-rest REST APis')
     app.run(debug=False, host=config['host'], port=config['port'])
   except SystemExit:
     camera.stop_preview()
     print('camera-rest shutdown.')
     pass
    

