# RPi Camera REST
This is simple REST service daemon, which allows access camera on Raspberry PI devices remotely.

![hw-arch](docs/rpi-camera-diagram.svg)

## Hardware
* Supported RPi devices: Raspberry Pi Zero WH, 2, 3, 4.
* Camera modules:
  * Raspberry Pi [Camera Module v2](https://www.raspberrypi.org/products/camera-module-v2/).
  * Raspberry Pi [NoIR Camera v2](https://www.raspberrypi.org/products/pi-noir-camera-v2/).
  * Waveshare [Camera Modules](https://www.waveshare.com/product/raspberry-pi/cameras.htm).
  * Waveshare [Camera Scheduler](https://www.waveshare.com/wiki/Camera_Scheduler).

## Install on Raspberry PI
1. Install [Raspberry Pi OS Lite 2021-05-28](https://downloads.raspberrypi.org/raspios_lite_armhf/images/raspios_lite_armhf-2021-05-28/)
2. Enable camera using ``sudo raspi-config``
3. Install software dependencies
   ```
   sudo apt install python-picamera python3-picamera
   sudo apt install python3-pip
   pip3 install RPi.GPIO
   ```
4. Copy files on Raspberry Pi device into directory ``/opt/camera`` 
5. Edit configuration file ``camera-rest.json``, 
   set http listening port, user credentials, camera resolutions and default settings. 
6. Install and enable ``camera-rest`` as systemd service.
   ```
   sudo cp camera-rest.service /etc/systemd/system/
   sudo chown root:root /etc/systemd/system/camera-rest.service
   sudo systemctl daemon-reload
   sudo systemctl enable camera-rest
   ```
7. Start | Stop ``camera-rest`` service.
   ```
   sudo systemctl start camera-rest
   sudo systemctl stop camera-rest
   sudo systemctl status camera-rest
   ```

## REST APIs and Endpoints
* [HTTP basic authentication](https://en.wikipedia.org/wiki/Basic_access_authentication) is required.
* Get info about RPi Camera device.  
  __GET__ ``/system/info``  
  ```
  curl -u client-001:ex4oo \
  http://<ip-address>:<port>/system/info
  ```
* Get current configuration  
  __GET__ ``/system/config``  
  ```
  curl -u client-001:ex4oo \ 
  http://<ip-address>:<port>/system/config
  ```
* Set camera capture configuration   
  __POST__ ``/system/config``  
  Supported config parameters  

  |  parameter    | type   | default | description                                   |
  |---------------|--------|---------|-------------------------------------------|
  | resolution    | string | "R1"    | enum, "R1", R2", "R3", ...                |
  | rotation      | int    | 0       | enum, degrees "D0", "D90", "D180", "D270" |
  | framerate     | int    | 24      | framerate                                 |

  ```
  curl -u client-001:ex4oo \
  --request POST \
  --url http://<ip-address>:<port>/system/config \
  --header 'Content-Type: application/json' \
  --data '{
    "resolution": "R1",
    "rotation": "D180",
    "framerate": 24
  }' 
  ```

* Select connected camera for capture. This endpoint is only effective if camera controller uses hardware 
  [Camera Scheduler](https://www.waveshare.com/wiki/Camera_Scheduler). Supported camera indexes: 0, 1.  
  __POST__ ``/system/camera``
  ```
  curl -u client-001:ex4oo \
  --request POST \
  --url http://<ip-address>:<port>/system/camera \
  --header 'Content-Type: application/json' \
  --data '{  "camera": 1 }'
  ```
* Get connected camera for capture.   
  __GET__ ``/system/camera``
  ```
  curl -u client-001:ex4oo \
  http://<ip-address>:<port>/system/camera 
  ```
* Get available camera resolutions.   
  __GET__ ``/system/resolutions``
  ```
  curl -u client-001:ex4oo \
  http://<ip-address>:<port>/system/resolutions 
  ```
* Get available camera rotations.   
  __GET__ ``/system/rotations``
  ```
  curl -u client-001:ex4oo \
  http://<ip-address>:<port>/system/resolutions 
  ```
* Capture single image and download it as attachment.   
  __GET__ ``/system/capture``  
  ```
  curl -u client-001:ex4oo \
  http://<ip-address>:<port>/system/capture --output snapshot.jpg
  ```  
* Capture video stream and download it as attachment.   
  __GET__ ``/system/stream.mjpg``
  ```
  curl -u client-001:ex4oo \
  http://<ip-address>:<port>/system/stream.mjpg --output video.mjpg
  ```    

### Hardware Assembly
![image-001](docs/image-001.jpg)

![image-002](docs/image-002.jpg)

![image-003](docs/image-003.jpg)
   
### References
* [picamera docs](https://picamera.readthedocs.io/en/latest/index.html)
* [Web streaming](http://picamera.readthedocs.io/en/latest/recipes2.html#web-streaming)

*Enjoy !*
