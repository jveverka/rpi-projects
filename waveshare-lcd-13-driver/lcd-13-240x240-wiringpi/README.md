# LCD driver
This is native implementation LCD display driver for waveshare 240x240 1.3" IPS LCD display.

## Build and run
This is how native driver is build and demo is started on Raspberry Pi device.
```
make clean
make 
make buildlib
./bin/lcd13InchDemo.o
```
Demo clears display and draws lines, pixels, circles on display.
Display activity must be visible.

#### Build results:
* __bin/lcd13InchDemo.o__ - standalone demo you can run directly on Raspberry Pi
* __bin/libLcd13Inch.so__ - shared library used by JNI applications
