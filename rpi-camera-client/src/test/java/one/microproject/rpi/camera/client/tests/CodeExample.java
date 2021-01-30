package one.microproject.rpi.camera.client.tests;

import one.microproject.rpi.camera.client.CameraClient;
import one.microproject.rpi.camera.client.CameraClientBuilder;
import one.microproject.rpi.camera.client.dto.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;

public class CodeExample {

    private static final Logger LOG = LoggerFactory.getLogger(CodeExample.class);

    public static void main(String[] args) throws IOException {
        CameraClient cameraClient = CameraClientBuilder.builder()
                .baseUrl("http://192.168.44.73:8080")
                .withCredentials("client-001", "ex4oo")
                .build();
        SystemInfo systemInfo = cameraClient.getSystemInfo();
        LOG.info("systemInfo: {} {}", systemInfo.getId(), systemInfo.getName());
        InputStream is = cameraClient.captureImage();
        File file = new File("/home/juraj/Downloads/camera.jpg");
        java.nio.file.Files.copy(
                is,
                file.toPath(),
                StandardCopyOption.REPLACE_EXISTING);
    }

}
