package one.microproject.rpi.device.dto.test;

import one.microproject.rpi.device.dto.SystemInfo;
import one.microproject.rpi.device.sim.DeviceSim;
import one.microproject.rpi.device.sim.DeviceSimBuilder;
import one.microproject.rpi.device.sim.dto.DataRequest;
import one.microproject.rpi.device.sim.dto.DataResponse;
import one.microproject.rpi.device.sim.dto.SimInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DeviceSimTest {

    private static DeviceSim deviceSim;

    @BeforeAll
    public static void init() {
        deviceSim = DeviceSimBuilder.builder()
                .withDeviceId("device-001")
                .build();
    }

    @Test
    void testSystemInfo() {
        SystemInfo<SimInfo> systemInfo = deviceSim.getSystemInfo();
        assertNotNull(systemInfo);
        assertNotNull(systemInfo.getId());
        assertNotNull(systemInfo.getType());
        assertNotNull(systemInfo.getVersion());
        assertEquals("device-001", systemInfo.getId());
    }

    @Test
    void testData() {
        DataResponse response = deviceSim.getData(new DataRequest("hi"));
        assertNotNull(response);
        assertNotNull(response.getMessage());
        assertEquals("hi", response.getMessage());
    }

    @Test
    void testDownload() throws IOException {
        InputStream is = deviceSim.download(new DataRequest("hi"));
        assertNotNull(is);
        is.close();
    }

    @Test
    void testDownloadGet() throws IOException {
        InputStream is = deviceSim.download();
        assertNotNull(is);
        is.close();
    }

    @Test
    void testDataGet() {
        DataResponse response = deviceSim.getData();
        assertNotNull(response);
        assertNotNull(response.getMessage());
        assertEquals("new-data", response.getMessage());
    }

}
