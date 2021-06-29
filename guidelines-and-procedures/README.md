# RPi quick notes
This notes is for [Raspberry Pi OS](https://downloads.raspberrypi.org/raspios_lite_armhf/images/).

## run script after startup
``sudo crontab -e`` add line ``@reboot /path/to/your/script.sh &``
