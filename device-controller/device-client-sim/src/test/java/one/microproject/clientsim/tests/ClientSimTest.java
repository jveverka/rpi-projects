package one.microproject.clientsim.tests;

import one.microproject.clientsim.ClientSim;
import one.microproject.clientsim.ClientSimBuilder;
import one.microproject.clientsim.dto.DataRequest;
import one.microproject.clientsim.dto.DataResponse;
import one.microproject.clientsim.dto.SystemInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClientSimTest {

    private static ClientSim clientSim;

    @BeforeAll
    public static void init() {
        clientSim = ClientSimBuilder.builder()
                .withDeviceId("device-001")
                .build();
    }

    @Test
    void testSystemInfo() {
        SystemInfo systemInfo = clientSim.getSystemInfo();
        assertNotNull(systemInfo);
        assertNotNull(systemInfo.id());
        assertNotNull(systemInfo.type());
        assertNotNull(systemInfo.version());
        assertEquals("device-001", systemInfo.id());
    }

    @Test
    void testData() {
        DataResponse response = clientSim.getData(new DataRequest("hi"));
        assertNotNull(response);
        assertNotNull(response.message());
        assertEquals("hi", response.message());
    }

    @Test
    void testDownload() throws IOException {
        InputStream is = clientSim.download(new DataRequest("hi"));
        assertNotNull(is);
        is.close();
    }

}
