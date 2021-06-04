package one.microproject.rpi.device.dto.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.rpi.device.dto.SystemInfo;
import one.microproject.rpi.device.sim.dto.SimInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SystemInfoSerializationTest {

    private static ObjectMapper mapper;
    private static Long started;

    @BeforeAll
    public static void init() {
        started = Instant.now().getEpochSecond();
        mapper = new ObjectMapper();
    }


    @Test
    void testSerializationAndDeserialization() throws JsonProcessingException {
        Long timestamp = Instant.now().getEpochSecond();
        Long uptime = timestamp - started;
        SimInfo info  =  new SimInfo("OK");
        SystemInfo<SimInfo> systemInfo = new SystemInfo<>("id-001", "device-sim", "1.0.0", "Rpi Device Simulator", timestamp, uptime, info);

        String systemInfoSerialized = mapper.writeValueAsString(systemInfo);

        SystemInfo<?> genericSystemInfo = mapper.readValue(systemInfoSerialized, SystemInfo.class);
        assertNotNull(genericSystemInfo);
        assertEquals("id-001", genericSystemInfo.getId());
        assertEquals("device-sim", genericSystemInfo.getType());
        assertEquals("1.0.0", genericSystemInfo.getVersion());
        assertEquals("Rpi Device Simulator", genericSystemInfo.getName());
        assertEquals(timestamp, genericSystemInfo.getTimestamp());
        assertEquals(uptime, genericSystemInfo.getUptime());

        SystemInfo<SimInfo> simSystemInfo = mapper.readValue(systemInfoSerialized, new TypeReference<SystemInfo<SimInfo>>() {});
        assertNotNull(simSystemInfo);
        assertEquals("id-001", simSystemInfo.getId());
        assertEquals("device-sim", simSystemInfo.getType());
        assertEquals("1.0.0", simSystemInfo.getVersion());
        assertEquals("Rpi Device Simulator", simSystemInfo.getName());
        assertEquals(timestamp, simSystemInfo.getTimestamp());
        assertEquals(uptime, simSystemInfo.getUptime());
        assertEquals("OK", simSystemInfo.getProperties().getStatus());
    }

}
