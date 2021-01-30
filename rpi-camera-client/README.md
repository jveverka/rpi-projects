[![Maven Central](https://maven-badges.herokuapp.com/maven-central/one.microproject.rpi/rpi-camera-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/one.microproject.rpi/rpi-camera-client)

# Java client for RPi Camera

This is Java client HTTP REST API SDK for [RPi Camera](../rpi-camera).

## Use, Compile and Install
* Use official [published artefacts](https://search.maven.org/search?q=one.microproject.rpi), or ...
* Clone this git repository.
* Run ``gradle clean build test publishToMavenLocal`` to install this jar locally.
* Use maven or gradle dependency below.

### use with maven
```
<dependency>
    <groupId>one.microproject.rpi</groupId>
    <artifactId>rpi-camera-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

### use with gradle
```
implementation 'one.microproject.rpi:rpi-camera-client:1.0.0'
```

### use in your java code
```java
CameraClient cameraClient = CameraClientBuilder.builder()
    .baseUrl("https://localhost:8090")
    .withCredentials("client-001", "secret")
    .build();
cameraClient.getSystemInfo();
cameraClient.captureImage();
```
