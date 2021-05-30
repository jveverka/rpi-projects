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
export ARCH=amd64|arm64v8
docker-compose up --build -d
docker logs -f device-controller_device-controller_1
docker-compose down -v --rmi all --remove-orphans
```

## Setup IAM-Service 
1. Get global admin tokens
2. Create device-controller project
3. Add device-admin permissions and role

## Build Dockers for x86_64 and ARM64 
```
# on x86 AMD64 device:
docker build -t jurajveverka/device-controller:1.0.0-amd64 --build-arg ARCH=amd64 --file ./device-controller/Dockerfile ./device-controller 
docker push jurajveverka/device-controller:1.0.0-amd64

# on ARM64 v8 device:
docker build -t jurajveverka/device-controller:1.0.0-arm64v8 --build-arg ARCH=arm64v8 --file ./device-controller/Dockerfile ./device-controller 
docker push jurajveverka/device-controller:1.0.0-arm64v8

# on x86 AMD64 device: 
docker manifest create \
jurajveverka/device-controller:1.0.0 \
--amend jurajveverka/device-controller:1.0.0-amd64 \
--amend jurajveverka/device-controller:1.0.0-arm64v8

docker manifest push jurajveverka/device-controller:1.0.0
```