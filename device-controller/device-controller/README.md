# Device Controller
Raspberry Pi controller gateway.

## Build and Run
```
gradle clean build test
docker-compose up --build -d
docker logs -f device-controller_device-controller_1
docker-compose down -v --rmi all --remove-orphans
```

## Build Docker image
```
docker build -t jurajveverka/device-controller:1.0.0 --file Dockerfile .
docker push jurajveverka/device-controller:1.0.0
```