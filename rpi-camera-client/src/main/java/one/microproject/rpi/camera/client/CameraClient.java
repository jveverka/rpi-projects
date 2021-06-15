package one.microproject.rpi.camera.client;

import one.microproject.rpi.camera.client.dto.CameraConfiguration;
import one.microproject.rpi.camera.client.dto.ImageCapture;
import one.microproject.rpi.camera.client.dto.CameraInfo;
import one.microproject.rpi.device.RPiDevice;
import one.microproject.rpi.device.dto.SystemInfo;


/**
 * Read data from RPi remote camera.
 * https://github.com/jveverka/rpi-projects/tree/master/rpi-camera
 */
public interface CameraClient extends RPiDevice<CameraInfo> {

    /**
     * Get {@link SystemInfo} for this RPi Camera client.
     * Contains information like unique ID of this device, type, version and description.
     * @return {@link CameraInfo}
     */
    SystemInfo<CameraInfo> getSystemInfo();

    /**
     * Get current camera configuration.
     * @return {@link CameraConfiguration}
     */
    CameraConfiguration getConfiguration();

    /**
     * Set current camera capture configuration.
     * @param configuration - new required {@link CameraConfiguration}.
     * @return actual effective {@link CameraConfiguration}.
     */
    CameraConfiguration setConfiguration(CameraConfiguration configuration);

    /**
     * Capture camera image using current {@link CameraConfiguration}.
     * @return image data as {@link ImageCapture}.
     */
    ImageCapture captureImage();

}
