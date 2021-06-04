package one.microproject.rpi.device.sim;

import one.microproject.rpi.device.sim.impl.DeviceSimImpl;

public class DeviceSimBuilder {

    private String deviceId;

    public DeviceSimBuilder withDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public DeviceSim build() {
        if (deviceId == null) {
            throw new UnsupportedOperationException();
        }
        return new DeviceSimImpl(deviceId);
    }

    public static DeviceSimBuilder builder() {
        return new DeviceSimBuilder();
    }

}
