package one.microproject.devicecontroller.service.devices;

import one.microproject.devicecontroller.dto.ClientAdapterWrapper;
import one.microproject.devicecontroller.dto.DeviceStatus;
import one.microproject.devicecontroller.dto.DeviceType;
import one.microproject.rpi.powercontroller.PowerControllerClient;

public class ClientAdapterPowerController implements ClientAdapterWrapper<PowerControllerClient> {

    private final PowerControllerClient powerControllerClient;
    private DeviceStatus status;

    public ClientAdapterPowerController(PowerControllerClient powerControllerClient) {
        this.powerControllerClient = powerControllerClient;
        this.status = DeviceStatus.OFFLINE;
    }

    @Override
    public PowerControllerClient getClient() {
        return powerControllerClient;
    }

    @Override
    public DeviceStatus getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(DeviceStatus deviceStatus) {
        this.status = deviceStatus;
    }

    @Override
    public DeviceType getType() {
        return DeviceType.RPI_POWER_CONTROLLER;
    }

}
