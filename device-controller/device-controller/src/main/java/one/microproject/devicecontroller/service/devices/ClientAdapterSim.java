package one.microproject.devicecontroller.service.devices;

import one.microproject.devicecontroller.dto.ClientAdapterWrapper;
import one.microproject.devicecontroller.dto.DeviceStatus;
import one.microproject.devicecontroller.dto.DeviceType;
import one.microproject.rpi.device.sim.DeviceSim;

public class ClientAdapterSim implements ClientAdapterWrapper<DeviceSim> {

    private final DeviceSim clientSim;
    private DeviceStatus status;

    public ClientAdapterSim(DeviceSim clientSim) {
        this.clientSim = clientSim;
        this.status = DeviceStatus.ONLINE;
    }

    @Override
    public DeviceSim getClient() {
        return clientSim;
    }

    @Override
    public DeviceStatus getStatus() {
        return this.status;
    }

    @Override
    public DeviceStatus checkStatus() {
        return this.status;
    }

    @Override
    public void setStatus(DeviceStatus deviceStatus) {
        this.status = deviceStatus;
    }

    @Override
    public DeviceType getType() {
        return DeviceType.DEVICE_SIM;
    }

}
