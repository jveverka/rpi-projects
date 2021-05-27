package one.microproject.devicecontroller.service.devices;

import one.microproject.devicecontroller.dto.ClientAdapterWrapper;
import one.microproject.devicecontroller.dto.DeviceStatus;
import one.microproject.devicecontroller.dto.DeviceType;
import one.microproject.rpi.camera.client.CameraClient;

public class ClientAdapterCamera implements ClientAdapterWrapper<CameraClient> {

    private final CameraClient cameraClient;
    private DeviceStatus status;

    public ClientAdapterCamera(CameraClient cameraClient) {
        this.cameraClient = cameraClient;
        this.status = DeviceStatus.OFFLINE;
    }

    @Override
    public CameraClient getClient() {
        return cameraClient;
    }

    @Override
    public DeviceStatus getStatus() {
        return this.status;
    }

    @Override
    public DeviceStatus checkStatus() {
        try {
            cameraClient.getSystemInfo();
            status = DeviceStatus.ONLINE;
        } catch (Exception e) {
            status = DeviceStatus.OFFLINE;
        }
        return status;
    }

    @Override
    public void setStatus(DeviceStatus deviceStatus) {
        this.status = deviceStatus;
    }

    @Override
    public DeviceType getType() {
        return DeviceType.RPI_CAMERA;
    }

}
