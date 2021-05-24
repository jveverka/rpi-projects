package one.microproject.clientsim;

import one.microproject.clientsim.impl.ClientSimImpl;

public class ClientSimBuilder {

    private String deviceId;

    public ClientSimBuilder withDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public ClientSim build() {
        if (deviceId == null) {
            throw new UnsupportedOperationException();
        }
        return new ClientSimImpl(deviceId);
    }

    public static ClientSimBuilder builder() {
        return new ClientSimBuilder();
    }

}
