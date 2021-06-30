# RPi quick notes
This notes is for [__Raspberry Pi OS__](https://www.raspberrypi.org/software/) 
* [Raspberry Pi OS Lite 32bit](https://downloads.raspberrypi.org/raspios_lite_armhf/images/).
* [Raspberry Pi OS Lite 64bit](https://downloads.raspberrypi.org/raspios_lite_arm64/images/). 

## run script after startup
```
sudo crontab -e`` add line ``@reboot /path/to/your/script.sh &
```

## get CPU temperature
```
vcgencmd measure_temp
```