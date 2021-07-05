package one.microproject.rpi.camera.client;

import one.microproject.rpi.camera.client.dto.CameraConfiguration;
import one.microproject.rpi.camera.client.dto.CameraSelect;
import one.microproject.rpi.camera.client.dto.ImageCapture;
import one.microproject.rpi.camera.client.dto.CameraInfo;
import one.microproject.rpi.camera.client.dto.Resolutions;
import one.microproject.rpi.camera.client.dto.Rotations;
import one.microproject.rpi.device.RPiDevice;
import one.microproject.rpi.device.dto.SystemInfo;

import java.io.InputStream;


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
     * Capture image using current {@link CameraConfiguration}.
     * @return image data as {@link ImageCapture}.
     */
    ImageCapture captureImage();

    /**
     * Capture video using current {@link CameraConfiguration}.
     * @return video data stream.
     */
    InputStream captureVideo();

    /**
     * Select connected camera. This method is only effective if camera controller uses hardware "camera scheduler" https://www.waveshare.com/wiki/Camera_Scheduler.
     * @param cameraSelectRequest select camera request.
     * @return index of selected camera.
     */
    CameraSelect selectCamera(CameraSelect cameraSelectRequest);

    /**
     * Get selected connected camera.
     * @return index of selected camera.
     */
    CameraSelect getSelectedCamera();

    /**
     * Get available camera resolutions.
     * @return available camera resolutions.
     */
    Resolutions getResolutions();

    /**
     * Get available camera rotations.
     * @return available camera rotations.
     */
    Rotations getRotations();

}
