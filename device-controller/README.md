# Device Controller
Raspberry Pi controller gateway.

![architecture](docs/device-controller.svg)

## Backend microservice(s)
* [__device-controller__](device-controller) - Raspberry Pi controller gateway.
* [__IAM-service__](https://github.com/jveverka/iam-service) - user auth. service.

## Compatible devices
* [__rpi-camera__](../rpi-camera) - RPi Camera project.
* [__rpi-powercontroller__](../rpi-powercontroller) - Remotely controller power switch. 
* [__device-client-sim__](device-client-sim) - Test device simulator.

## Build and Run
```
gradle clean build test
docker-compose up --build -d
docker logs -f device-controller_device-controller_1
docker-compose down -v --rmi all --remove-orphans
```

## Build Docker image
```
docker build -t jurajveverka/device-controller:1.0.0 --file device-controller/Dockerfile .
docker push jurajveverka/device-controller:1.0.0
```