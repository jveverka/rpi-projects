package one.microproject.rpi.device.sim.impl;

import one.microproject.rpi.device.dto.SystemInfo;
import one.microproject.rpi.device.sim.DeviceSim;
import one.microproject.rpi.device.sim.dto.DataRequest;
import one.microproject.rpi.device.sim.dto.DataResponse;
import one.microproject.rpi.device.sim.dto.SimInfo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;

public class DeviceSimImpl implements DeviceSim {

    private final String id;
    private final Long started;

    public DeviceSimImpl(String id) {
        this.id = id;
        this.started = Instant.now().getEpochSecond();
    }

    @Override
    public SystemInfo<SimInfo> getSystemInfo() {
        Long timestamp = Instant.now().getEpochSecond();
        Long uptime = timestamp - started;
        SimInfo info  =  new SimInfo("OK");
        return new SystemInfo<>(id, "device-sim", "1.0.0", "Rpi Device Simulator", timestamp, uptime, info);
    }

    @Override
    public DataResponse getData(DataRequest request) {
        return new DataResponse(request.getMessage());
    }

    @Override
    public InputStream download(DataRequest request) {
        return new ByteArrayInputStream(request.getMessage().getBytes());
    }

}
