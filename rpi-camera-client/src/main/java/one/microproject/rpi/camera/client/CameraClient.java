package one.microproject.rpi.camera.client;

import one.microproject.rpi.camera.client.dto.SystemInfo;

import java.io.InputStream;

public interface CameraClient {

    SystemInfo getSystemInfo();

    InputStream captureImage();

}
