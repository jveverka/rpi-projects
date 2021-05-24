package one.microproject.devicecontroller.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import one.microproject.clientsim.dto.DataRequest;
import one.microproject.clientsim.dto.DataResponse;
import one.microproject.clientsim.dto.SystemInfo;
import one.microproject.devicecontroller.dto.DeviceCreateRequest;
import one.microproject.devicecontroller.dto.DeviceCredentials;
import one.microproject.devicecontroller.dto.DeviceInfo;
import one.microproject.devicecontroller.dto.DeviceQuery;
import one.microproject.devicecontroller.dto.DeviceQueryResponse;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = AdminApiTest.Initializer.class)
class AdminApiTest {

    private static final Logger LOG = LoggerFactory.getLogger(AdminApiTest.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @Order(1)
    void getDevices() {
        ResponseEntity<DeviceInfo[]> responseEntity = restTemplate.getForEntity("/api/admin/devices", DeviceInfo[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(0, responseEntity.getBody().length);
    }

    @Test
    @Order(2)
    void addDevices() {
        DeviceCredentials credentials = new DeviceCredentials("admin", "secret");
        DeviceCreateRequest request = new DeviceCreateRequest("device-01", "device-sim", "http://host:8080/data", credentials, "group-001");
        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/admin/devices", request, Void.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        credentials = new DeviceCredentials("admin", "secret");
        request = new DeviceCreateRequest("device-02", "device-sim", "http://host:8080/data", credentials, "group-001");
        responseEntity = restTemplate.postForEntity("/api/admin/devices", request, Void.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @Order(3)
    void checkDevices() {
        ResponseEntity<DeviceInfo[]> responseEntity = restTemplate.getForEntity("/api/admin/devices", DeviceInfo[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(2, responseEntity.getBody().length);
    }

    @Test
    @Order(4)
    void testQueryDevice() throws JsonProcessingException {
        DeviceQuery deviceQuery = new DeviceQuery("q-01", "device-01", "system-info", null);
        ResponseEntity<DeviceQueryResponse> responseEntity = restTemplate.postForEntity("/api/data/devices/query", deviceQuery, DeviceQueryResponse.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        DeviceQueryResponse response = responseEntity.getBody();
        assertNotNull(response);
        SystemInfo systemInfo = objectMapper.treeToValue(response.payload(), SystemInfo.class);
        assertNotNull(systemInfo);
        assertNotNull(systemInfo.id());
        assertNotNull(systemInfo.type());
        assertNotNull(systemInfo.version());

        ObjectNode objectNode = objectMapper.valueToTree(new DataRequest("hi"));
        deviceQuery = new DeviceQuery("q-01", "device-01", "data", objectNode);
        responseEntity = restTemplate.postForEntity("/api/data/devices/query", deviceQuery, DeviceQueryResponse.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        response = responseEntity.getBody();
        assertNotNull(response);
        DataResponse dataResponse = objectMapper.treeToValue(response.payload(), DataResponse.class);
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.message());
        assertEquals("hi", dataResponse.message());
    }

    @Test
    @Order(5)
    void deleteDevice() {
        restTemplate.delete("/api/admin/devices/" + "device-01");
        ResponseEntity<DeviceInfo[]> responseEntity = restTemplate.getForEntity("/api/admin/devices", DeviceInfo[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, responseEntity.getBody().length);
    }

    @Test
    @Order(6)
    void deleteLastDevice() {
        restTemplate.delete("/api/admin/devices/" + "device-02");
        ResponseEntity<DeviceInfo[]> responseEntity = restTemplate.getForEntity("/api/admin/devices", DeviceInfo[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(0, responseEntity.getBody().length);
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.4-bionic");
        private static void startContainers() {
            Startables.deepStart(Stream.of(mongoDBContainer)).join();
        }
        private static Map<String, String> createContextConfiguration() {
            return Map.of(
                    "spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl()
            );
        }
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            startContainers();
            LOG.info("MongoDB URI: {}", mongoDBContainer.getReplicaSetUrl());
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            MapPropertySource testContainers = new MapPropertySource("testcontainers", (Map) createContextConfiguration());
            environment.getPropertySources().addFirst(testContainers);
        }
    }

}
