package one.microproject.rpi.camera.client.tests;

import one.microproject.rpi.camera.client.CameraClient;
import one.microproject.rpi.camera.client.CameraClientBuilder;
import one.microproject.rpi.camera.client.dto.CameraConfiguration;
import one.microproject.rpi.camera.client.dto.ImageCapture;
import one.microproject.rpi.camera.client.dto.CameraInfo;
import one.microproject.rpi.device.dto.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

public class CodeExample {

    private static final Logger LOG = LoggerFactory.getLogger(CodeExample.class);

    public static void main(String[] args) throws IOException {
        String baseUrl = "http://192.168.44.164:8080";
        CameraClient cameraClient = CameraClientBuilder.builder()
                .baseUrl(baseUrl)
                .setTimeout(80L, TimeUnit.SECONDS)
                .withCredentials("client-001", "ex4oo")
                .build();
        SystemInfo<CameraInfo> systemInfo = cameraClient.getSystemInfo();
        LOG.info("systemInfo: {} {} {} {} {}", systemInfo.getId(), systemInfo.getName(), systemInfo.getVersion(), systemInfo.getTimestamp(), systemInfo.getUptime());
        CameraConfiguration configuration = CameraConfiguration.getDefault();
        configuration = cameraClient.setConfiguration(configuration);
        LOG.info("config: {} {} {}", configuration.getResolution(), configuration.getRotation(), configuration.getFramerate());
        ImageCapture imageCapture = cameraClient.captureImage();
        File file = new File("/home/juraj/Downloads/rpi-camera-test.jpg");
        Files.copy(imageCapture.getIs(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

}
