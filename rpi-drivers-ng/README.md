
1. Build locally
   ```
   gradle clean build test publishToMavenLocal installDist distZip
   ```
2. Copy to raspberry
   ```
   scp run-sensors-test.sh pi@192.168.44.73:/home/pi/
   scp ~/.m2/repository/one/microproject/rpi/rpi-drivers/2.0.0/rpi-drivers-2.0.0.jar pi@192.168.44.73:/home/pi/
   scp build/distributions/rpi-drivers-ng-2.0.0.zip pi@192.168.44.178:/home/pi/
   ```