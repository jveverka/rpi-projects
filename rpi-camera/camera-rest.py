#!/usr/bin/env python3

import time
import picamera
import json
import sys
import io
from flask import Flask, request, jsonify, send_file
from flask_httpauth import HTTPBasicAuth
from werkzeug.security import generate_password_hash, check_password_hash

print('RPi Camera REST')
print('app initialization started')
camera = picamera.PiCamera()
app = Flask(__name__)
auth = HTTPBasicAuth()
cf = open(sys.argv[1])
config = json.load(cf)
started = int(time.time())

camera_resolutions = {
  "8M" : {
     "image_width": 3280,
     "image_height": 2464
  },
  "5M" : {
     "image_width": 2592,
     "image_height": 1944
  },
  "2M" : {
     "image_width": 1920,
     "image_height": 1440
  },
  "1M" : {
     "image_width": 1280,
     "image_height": 960
  }
}

print('loading credentials ...')
users = {}
for user in config['credentials']:
    print ("user name: " + user)
    users[user] = generate_password_hash(config['credentials'][user])

print('app initialization done')

@auth.verify_password
def verify_password(username, password):
    if username in users and check_password_hash(users.get(username), password):
        return username

@app.route('/system/info', methods=["GET"])
@auth.login_required
def getVersion():
    uptime = int(time.time()) - started
    version = { "id": config['id'], "type": "camera-rest", "version": "1.4.0", "name": config['name'], "timestamp": int(time.time()), "uptime": uptime, "properties": { "revision": camera.revision } }
    return jsonify(version)

@app.route('/system/capture', methods=["GET"])
@auth.login_required
def capture():
    print('capture')
    file_format = 'jpeg'
    file_name='capture-image.jpg'
    default_resolution = '5M'
    args = request.args
    rotation = 0

    if 'rotation' in args:
       rotation = int(args['rotation'])
       print('rotation=' + str(rotation))

    if 'shutter-speed' in args:
       shutter_speed = float(args['shutter-speed'])
       print('shutter_speed = ' + str(shutter_speed))
       camera.shutter_speed = int(shutter_speed * 1000)
    else:
       camera.shutter_speed = 0

    if 'format' in args:
       file_format = args['format']
       file_name='capture-image.' +  file_format
       print('format = ' + file_format)

    if 'resolution' in args:
       default_resolution = args['resolution']
       print('resolution = ' + default_resolution + ' ' +
          str(camera_resolutions[default_resolution]['image_width']) + 'x' +
          str(camera_resolutions[default_resolution]['image_height'])
       )

    camera.resolution = (
       camera_resolutions[default_resolution]['image_width'],
       camera_resolutions[default_resolution]['image_height']
    )
    camera.framerate = 15
    camera.rotation = rotation
    camera.start_preview()
    image_buffer = io.BytesIO()
    camera.capture(image_buffer, file_format)
    camera.stop_preview()

    image_buffer.flush()
    image_buffer.seek(0, 0)
    return send_file(image_buffer, attachment_filename=file_name, as_attachment=True)

if __name__ == '__main__':
    try:
        print('camera-rest init.')
        print('#CONFIG Id: ' + config['id'])
        print('#CONFIG name: ' + config['name'])
        print('camera-rest REST APis')
        app.run(debug=False, host=config['host'], port=config['port'])
    except SystemExit:
        camera.close()
        print('camera-rest shutdown.')
        pass
