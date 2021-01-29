[![Maven Central](https://maven-badges.herokuapp.com/maven-central/one.microproject.rpi/rpi-powercontroller-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/one.microproject.rpi/rpi-powercontroller-client)

# Java client for RPi Power Controller

This is Java client SDK for [RPi Power Controller](../rpi-powercontroller). 

## Use, Compile and Install
* Use official [published artefacts](https://search.maven.org/search?q=one.microproject.rpi), or ...
* Clone this git repository.
* Run ``gradle clean build test publishToMavenLocal`` to install this jar locally.
* Use maven or gradle dependency below.

### use with maven
```
<dependency>
    <groupId>one.microproject.rpi</groupId>
    <artifactId>rpi-powercontroller-client</artifactId>
    <version>1.2.0</version>
</dependency>
```

### use with gradle
```
implementation 'one.microproject.rpi:rpi-powercontroller-client:1.2.0'
```

### use in your java code
```java
PowerControllerReadClient powerControllerReadClient = PowerControllerClientBuilder.builder()
        .baseUrl("http://localhost:8090")
        .withCredentials("client-001", "secret")
        .buildReadClient();
powerControllerReadClient.getSystemInfo();
...
```
```java
PowerControllerClient powerControllerClient = PowerControllerClientBuilder.builder()
        .baseUrl("http://localhost:8090")
        .withCredentials("client-001", "secret")
        .build();
powerControllerClient.getSystemInfo();
powerControllerClient.getMeasurements();
...
```
