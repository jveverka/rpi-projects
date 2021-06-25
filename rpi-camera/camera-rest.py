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
started = int(time.time())
camera = picamera.PiCamera()
app = Flask(__name__)
auth = HTTPBasicAuth()
cf = open(sys.argv[1])

config = json.load(cf)
defaults = config['defaults']
file_name = 'capture-image.jpg'

camera_resolutions = {
  "M8" : {
     "image_width": 3280,
     "image_height": 2464
  },
  "M5" : {
     "image_width": 2592,
     "image_height": 1944
  },
  "M2" : {
     "image_width": 1920,
     "image_height": 1440
  },
  "M1" : {
     "image_width": 1280,
     "image_height": 960
  },
  "M03" : {
     "image_width": 720,
     "image_height": 480
  }
}

camera_rotations = {
  "D0": 0,
  "D90": 90,
  "D180": 180,
  "D270": 270
}

image_formats = {
   "JPEG": "jpeg",
   "PNG": "png"
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
    version = { "id": config['id'], "type": "camera-rest", "version": "1.5.0", "name": config['name'], "timestamp": int(time.time()), "uptime": uptime, "properties": { "revision": camera.revision } }
    return jsonify(version)

@app.route('/system/config', methods=["POST"])
@auth.login_required
def set_config():
    print('set capture parameters')
    body = request.json
    set_camera(body)
    return jsonify(defaults)

@app.route('/system/config', methods=["GET"])
@auth.login_required
def get_config():
    return jsonify(defaults)

@app.route('/system/capture', methods=["GET"])
@auth.login_required
def capture():
    print('capture')
    image_buffer = io.BytesIO()
    camera.capture(output=image_buffer, format=image_formats[defaults['imageFormat']], quality=int(defaults['quality']))
    image_buffer.flush()
    image_buffer.seek(0, 0)
    return send_file(image_buffer, attachment_filename=file_name, as_attachment=True)


def set_camera(body):
    print('set_camera: stopping preview ...')
    camera.stop_preview()

    if 'rotation' in body:
       defaults['rotation'] = body['rotation']
       print('rotation=' + str(camera_rotations[defaults['rotation']]))

    if 'shutterSpeed' in body:
       defaults['shutterSpeed'] = body['shutterSpeed']
       print('shutterSpeed = ' + str(defaults['shutterSpeed']))
       camera.shutter_speed = int(defaults['shutterSpeed'] * 1000)
    else:
       camera.shutter_speed = 0

    if 'imageFormat' in body:
       defaults['imageFormat'] = body['imageFormat']
       file_name='capture-image.' +  image_formats[defaults['imageFormat']]
       print('imageFormat = ' + image_formats[defaults['imageFormat']])
       print('file_name = ' + file_name)

    if 'resolution' in body:
       defaults['resolution'] = body['resolution']
       print('resolution = ' + defaults['resolution'] + ' ' +
          str(camera_resolutions[defaults['resolution']]['image_width']) + 'x' +
          str(camera_resolutions[defaults['resolution']]['image_height'])
       )

    if 'quality' in body:
       defaults['quality'] = body['quality']
       print('quality = ' + str(defaults['quality']))

    camera.resolution = (
       camera_resolutions[defaults['resolution']]['image_width'],
       camera_resolutions[defaults['resolution']]['image_height']
    )
    camera.framerate = 30
    camera.rotation = camera_rotations[defaults['rotation']]
    print('set_camera: starting preview ...')
    camera.start_preview()


if __name__ == '__main__':
    try:
        print('camera-rest init.')
        print('#CONFIG Id: ' + config['id'])
        print('#CONFIG name: ' + config['name'])
        print('#CONFIG defaults.imageFormat: ' + str(defaults['imageFormat']))
        print('#CONFIG defaults.rotation: ' + str(defaults['rotation']))
        print('#CONFIG defaults.quality: ' + str(defaults['quality']))
        print('#CONFIG defaults.resolution: ' + str(defaults['resolution']))
        print('#CONFIG defaults.shutterSpeed: ' + str(defaults['shutterSpeed']))
        set_camera(defaults)
        app.run(debug=False, host=config['host'], port=config['port'])
    except SystemExit:
        camera.close()
        print('camera-rest shutdown.')
        pass
