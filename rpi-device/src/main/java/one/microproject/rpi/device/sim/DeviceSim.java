package one.microproject.rpi.device.sim;

import one.microproject.rpi.device.RPiDevice;
import one.microproject.rpi.device.dto.SystemInfo;
import one.microproject.rpi.device.sim.dto.DataRequest;
import one.microproject.rpi.device.sim.dto.DataResponse;
import one.microproject.rpi.device.sim.dto.SimInfo;

import java.io.InputStream;

public interface DeviceSim extends RPiDevice<SimInfo> {

    SystemInfo<SimInfo> getSystemInfo();

    DataResponse getData(DataRequest request);

    InputStream download(DataRequest request);

}
