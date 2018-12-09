# RaspberryPi SenseHAT games
This is simple implementation of selected RaspberryPi SenseHAT games in python3.
* pong game
* snake game
* tetris game
* invaders game
* colors - random color generator
* gyro test app
* chritmas app
* sleep mode app
* sensehat-launcher.py - simple menu to launch the games

## Hardware requirements
- Raspberry PI 2, 3, zero
- [Sense hat](https://www.raspberrypi.org/products/sense-hat/) installed

### Install & Setup
```
sudo apt-get install python3-pip
sudo apt-get install sense-hat
sudo apt-get install python3-pygame
pip3 install pygame
```

### Setup to autorun on startup
```
sudo cp /opt/sensehat-games/sensehat.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable sensehat
sudo systemctl start sensehat
sudo systemctl status sensehat
sudo systemctl stop sensehat
```

