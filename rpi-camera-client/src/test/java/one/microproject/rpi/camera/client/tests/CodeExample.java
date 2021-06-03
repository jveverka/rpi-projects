package one.microproject.rpi.camera.client.tests;

import one.microproject.rpi.camera.client.CameraClient;
import one.microproject.rpi.camera.client.CameraClientBuilder;
import one.microproject.rpi.camera.client.dto.CaptureRequest;
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
        CameraClient cameraClient = CameraClientBuilder.builder()
                .baseUrl("http://192.168.44.164:8080")
                .setTimeout(80L, TimeUnit.SECONDS)
                .withCredentials("client-001", "ex4oo")
                .build();
        SystemInfo<CameraInfo> systemInfo = cameraClient.getSystemInfo();
        LOG.info("systemInfo: {} {} {} {} {}", systemInfo.getId(), systemInfo.getName(), systemInfo.getVersion(), systemInfo.getTimestamp(), systemInfo.getUptime());
        ImageCapture imageCapture = cameraClient.captureImage(CaptureRequest.getDefault());
        File file = new File("/home/juraj/Downloads/camera-test.jpg");
        Files.copy(imageCapture.getIs(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

}
