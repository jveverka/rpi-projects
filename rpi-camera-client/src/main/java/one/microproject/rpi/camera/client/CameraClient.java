package one.microproject.rpi.camera.client;

import one.microproject.rpi.camera.client.dto.ImageCapture;
import one.microproject.rpi.camera.client.dto.ImageFormat;
import one.microproject.rpi.camera.client.dto.Resolution;
import one.microproject.rpi.camera.client.dto.SystemInfo;


/**
 * Read data from RPi remote camera.
 * https://github.com/jveverka/rpi-projects/tree/master/rpi-camera
 */
public interface CameraClient {

    /**
     * Get {@link SystemInfo} for this RPi Power Controller.
     * Contains information like unique ID of this device, type, version and description.
     * @return {@link SystemInfo}
     */
    SystemInfo getSystemInfo();

    /**
     * Capture camera image in JPEG format.
     * @return JPEG image data as {@link ImageCapture}.
     */
    ImageCapture captureImage();

    /**
     * Capture camera image in JPEG format.
     * @param shutterSpeed in milliseconds, 0=automatic.
     * @return JPEG image data as {@link ImageCapture}.
     */
    ImageCapture captureImage(Float shutterSpeed);

    /**
     * Capture camera image in selected format.
     * @param shutterSpeed in milliseconds, 0=automatic.
     * @param imageFormat selected image format.
     * @return image data as {@link ImageCapture}.
     */
    ImageCapture captureImage(Float shutterSpeed, ImageFormat imageFormat);


    /**
     * Capture camera image in selected format.
     * @param shutterSpeed in milliseconds, 0=automatic.
     * @param imageFormat selected image format.
     * @param resolution image resolution in megapixels: 1M, 2M, 5M, 8M.
     * @return image data as {@link ImageCapture}.
     */
    ImageCapture captureImage(Float shutterSpeed, ImageFormat imageFormat, Resolution resolution);

    /**
     * Capture camera image in JPEG format.
     * @param shutterSpeed in milliseconds, 0=automatic.
     * @param resolution image resolution in megapixels: 1M, 2M, 5M, 8M.
     * @return JPEG image data as {@link ImageCapture}.
     */
    ImageCapture captureImage(Float shutterSpeed, Resolution resolution);

}
