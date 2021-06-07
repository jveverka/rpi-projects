ARG ARCH="amd64"
FROM ${ARCH}/adoptopenjdk:16-jre
COPY build/libs/device-controller-1.4.4-SNAPSHOT.jar /device-controller.jar
COPY device-controller-start.sh /device-controller-start.sh
COPY src/main/resources/application.yml /application.yml
COPY src/main/resources/application-cloud.yml /application-cloud.yml
RUN chmod +x /device-controller-start.sh
ENV XMX 128m
ENV APP_CONFIG_PATH /application-cloud.yml
ENV JVM_OPTS ""
ENTRYPOINT ["/device-controller-start.sh"]
