package one.microproject.devicecontroller.service.devices;

import one.microproject.clientsim.ClientSim;
import one.microproject.devicecontroller.dto.ClientAdapterWrapper;
import one.microproject.devicecontroller.dto.DeviceStatus;
import one.microproject.devicecontroller.dto.DeviceType;

public class ClientAdapterSim implements ClientAdapterWrapper<ClientSim> {

    private final ClientSim clientSim;
    private DeviceStatus status;

    public ClientAdapterSim(ClientSim clientSim) {
        this.clientSim = clientSim;
        this.status = DeviceStatus.OFFLINE;
    }

    @Override
    public ClientSim getClient() {
        return clientSim;
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
        return DeviceType.DEVICE_SIM;
    }

}
