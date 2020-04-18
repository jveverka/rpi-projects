package itx.rpi.powercontroller.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import itx.rpi.powercontroller.PowerControllerApp;
import itx.rpi.powercontroller.config.Configuration;
import itx.rpi.powercontroller.dto.Measurements;
import itx.rpi.powercontroller.dto.SystemInfo;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PowerControllerTests {

    private static final Logger LOG = LoggerFactory.getLogger(PowerControllerTests.class);

    private static final String BASE_URL = "http://localhost:8080/";

    private static CloseableHttpClient httpClient;
    private static PowerControllerApp.Services services;
    private static ExecutorService executorService;
    private static ObjectMapper mapper;
    private static Configuration configuration;

    @BeforeAll
    public static void init() throws IOException {
        httpClient = HttpClients.createDefault();
        executorService = Executors.newSingleThreadExecutor();
        mapper = new ObjectMapper();
        InputStream is = PowerControllerApp.class.getResourceAsStream("/configuration.json");
        configuration = mapper.readValue(is, Configuration.class);
        services = PowerControllerApp.initialize(mapper, configuration);
        executorService.submit(() -> services.getServer().start());
        for (int i=0; i<10; i++) {
            try {
                LOG.info("Waiting for server: ");
                Thread.sleep(1000);
                HttpGet get = new HttpGet(BASE_URL + "/system/info");
                httpClient.execute(get);
                break;
            } catch (HttpHostConnectException e) {
                LOG.error("Server not running: ", e);
            } catch (InterruptedException e) {
                LOG.error("Exception", e);
            }
        }
    }

    @Test
    @Order(1)
    public void testSystemInfo() throws IOException {
        HttpGet get = new HttpGet(BASE_URL + "/system/info");
        CloseableHttpResponse response = httpClient.execute(get);
        assertEquals(200, response.getStatusLine().getStatusCode());
        SystemInfo systemInfo = mapper.readValue(response.getEntity().getContent(), SystemInfo.class);
        assertNotNull(systemInfo);
        assertEquals(configuration.getId(), systemInfo.getId());
        assertNotNull(systemInfo.getVersion());
        assertNotNull(systemInfo.getName());
        assertNotNull(systemInfo.getType());
    }

    @Test
    @Order(2)
    public void testSystemMeasurements() throws IOException {
        HttpGet get = new HttpGet(BASE_URL + "/system/measurements");
        CloseableHttpResponse response = httpClient.execute(get);
        assertEquals(200, response.getStatusLine().getStatusCode());
        Measurements measurements = mapper.readValue(response.getEntity().getContent(), Measurements.class);
        assertNotNull(measurements);
        assertNotNull(measurements.getPressureUnit());
        assertNotNull(measurements.getRelHumidityUnit());
        assertNotNull(measurements.getTemperatureUnit());
        assertNull(measurements.getPressure());
        assertNull(measurements.getRelHumidity());
        assertNull(measurements.getTemperature());
    }

    @AfterAll
    public static void shutdown() throws Exception {
        httpClient.close();
        services.getServer().stop();
        services.shutdown();
        executorService.shutdown();
    }

}
