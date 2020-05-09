#!/usr/bin/env python3

import picamera
import json
import sys
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

@app.route('/info', methods=["GET"])
@auth.login_required
def getVersion():
    version = { "id": config['id'], "type": "camera-rest", "version": "1.0.0", "name": config['name'] }
    return jsonify(version)

@app.route('/capture', methods=["GET"])
@auth.login_required
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

