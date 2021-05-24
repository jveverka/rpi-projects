package one.microproject.clientsim.impl;

import one.microproject.clientsim.ClientSim;
import one.microproject.clientsim.dto.DataRequest;
import one.microproject.clientsim.dto.DataResponse;
import one.microproject.clientsim.dto.SystemInfo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ClientSimImpl implements ClientSim {

    private final String deviceId;

    public ClientSimImpl(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public SystemInfo getSystemInfo() {
        return new SystemInfo(deviceId, "device-sim", "1.0.0");
    }

    @Override
    public DataResponse getData(DataRequest request) {
        return new DataResponse(request.message());
    }

    @Override
    public InputStream download(DataRequest request) {
        return new ByteArrayInputStream(request.message().getBytes());
    }

}
