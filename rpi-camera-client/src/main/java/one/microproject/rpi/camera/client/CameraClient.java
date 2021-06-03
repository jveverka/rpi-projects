package one.microproject.rpi.camera.client;

import one.microproject.rpi.camera.client.dto.CaptureRequest;
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
     * Capture camera image in JPEG format with default parameters.
     * @return JPEG image data as {@link ImageCapture}.
     */
    ImageCapture captureImage();


    /**
     * Capture camera image in selected format.
     * @param request image capture parameters.
     * @return image data as {@link ImageCapture}.
     */
    ImageCapture captureImage(CaptureRequest request);

}
