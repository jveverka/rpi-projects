package one.microproject.devicecontroller.tests;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import one.microproject.devicecontroller.config.IAMClientConfiguration;
import one.microproject.devicecontroller.config.security.IAMSecurityFilterConfiguration;
import one.microproject.devicecontroller.dto.DeviceCreateRequest;
import one.microproject.devicecontroller.dto.DeviceCredentials;
import one.microproject.devicecontroller.dto.DeviceInfo;
import one.microproject.devicecontroller.dto.DeviceQuery;
import one.microproject.devicecontroller.dto.DeviceQueryResponse;
import one.microproject.iamservice.core.dto.CreateRole;
import one.microproject.iamservice.core.dto.PermissionInfo;
import one.microproject.iamservice.core.dto.StandardTokenClaims;
import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.dto.TokenResponseWrapper;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.UserProperties;
import one.microproject.iamservice.core.services.dto.SetupOrganizationRequest;
import one.microproject.iamservice.core.services.dto.SetupOrganizationResponse;
import one.microproject.iamservice.core.utils.ModelUtils;
import one.microproject.iamservice.serviceclient.IAMAuthorizerClient;
import one.microproject.iamservice.serviceclient.IAMServiceClientBuilder;
import one.microproject.iamservice.serviceclient.IAMServiceManagerClient;
import one.microproject.iamservice.serviceclient.IAMServiceProjectManagerClient;
import one.microproject.iamservice.serviceclient.IAMServiceUserManagerClient;
import one.microproject.iamservice.serviceclient.impl.AuthenticationException;
import one.microproject.rpi.device.dto.SystemInfo;
import one.microproject.rpi.device.sim.dto.DataRequest;
import one.microproject.rpi.device.sim.dto.DataResponse;
import one.microproject.rpi.device.sim.dto.SimInfo;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.shaded.okhttp3.MediaType;
import org.testcontainers.shaded.okhttp3.OkHttpClient;
import org.testcontainers.shaded.okhttp3.Request;
import org.testcontainers.shaded.okhttp3.RequestBody;
import org.testcontainers.shaded.okhttp3.Response;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = DeviceControllerTest.Initializer.class)
public class DeviceControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceControllerTest.class);
    private static final int DOCKER_EXPOSED_MONGO_PORT = 27017;
    private static final int IAM_SERVICE_EXPOSED_PORT = 8080;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final OkHttpClient httpClient = new OkHttpClient();

    private static String iamServiceBaseURL;
    private static IAMServiceManagerClient iamServiceManagerClient;
    private static IAMAuthorizerClient iamAuthorizerClient;

    private static TokenResponse globalAdminTokens;
    private static TokenResponse projectAdminTokens;

    private final static Set<PermissionInfo> adminPermissions = Set.of(
            PermissionInfo.from("device-controller", "devices", "read"),
            PermissionInfo.from("device-controller", "devices", "write"),
            PermissionInfo.from("device-controller", "data", "read"),
            PermissionInfo.from("device-controller", "data", "write")
    );

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    IAMSecurityFilterConfiguration iamSecurityFilterConfiguration;

    @Autowired
    IAMClientConfiguration configuration;

    @Test
    @Order(1)
    void getPrepareIAMService() throws IOException {
        iamServiceManagerClient = IAMServiceClientBuilder.builder()
                .withBaseUrl(new URL(iamServiceBaseURL))
                .withConnectionTimeout(60L, TimeUnit.SECONDS)
                .build();
        TokenResponseWrapper tokenResponseWrapper = iamServiceManagerClient
                .getIAMAdminAuthorizerClient()
                .getAccessTokensOAuth2UsernamePassword("admin", "secret", ModelUtils.IAM_ADMIN_CLIENT_ID, "top-secret");
        assertTrue(tokenResponseWrapper.isOk());
        globalAdminTokens = tokenResponseWrapper.getTokenResponse();
        assertNotNull(globalAdminTokens);
        assertNotNull(globalAdminTokens.getAccessToken());
        assertNotNull(globalAdminTokens.getRefreshToken());
    }

    @Test
    @Order(2)
    void setupProjectUsers() throws AuthenticationException {
        SetupOrganizationRequest request = new SetupOrganizationRequest(configuration.getOrganizationId().getId(), "",
                configuration.getProjectId().getId(), "", "admin-client", "acs",
                "admin", "7s4sa5", "", Set.of(configuration.getProjectId().getId()), "", new UserProperties(Map.of()));
        SetupOrganizationResponse setupOrganizationResponse = iamServiceManagerClient.setupOrganization(globalAdminTokens.getAccessToken(), request);
        assertNotNull(setupOrganizationResponse);
    }

    @Test
    @Order(3)
    void setupProjectPermissionsAndRoles() throws AuthenticationException, IOException {
        iamAuthorizerClient = iamServiceManagerClient.getIAMAuthorizerClient(configuration.getOrganizationId(), configuration.getProjectId());
        TokenResponseWrapper tokenResponseWrapper = iamAuthorizerClient.getAccessTokensOAuth2UsernamePassword("admin", "7s4sa5", ClientId.from("admin-client"), "acs");
        assertTrue(tokenResponseWrapper.isOk());
        projectAdminTokens = tokenResponseWrapper.getTokenResponse();

        IAMServiceProjectManagerClient iamServiceProjectClient = iamServiceManagerClient.getIAMServiceProject(projectAdminTokens.getAccessToken(), configuration.getOrganizationId(), configuration.getProjectId());
        CreateRole createDeviceAdminRole = new CreateRole("device-admin", "Device Admin", adminPermissions);
        iamServiceProjectClient.createRole(createDeviceAdminRole);
        IAMServiceUserManagerClient iamServiceUserManagerClient = iamServiceManagerClient.getIAMServiceUserManagerClient(projectAdminTokens.getAccessToken(), configuration.getOrganizationId(), configuration.getProjectId());
        iamServiceUserManagerClient.addRoleToUser(UserId.from("admin"), RoleId.from("device-admin"));
    }


    @Test
    @Order(4)
    void getAccessTokens() throws IOException {
        TokenResponseWrapper tokenResponseWrapper = iamAuthorizerClient.getAccessTokensOAuth2UsernamePassword("admin", "7s4sa5", ClientId.from("admin-client"), "acs");
        assertTrue(tokenResponseWrapper.isOk());
        projectAdminTokens = tokenResponseWrapper.getTokenResponse();

        boolean updated = iamSecurityFilterConfiguration.getIamClient().updateKeyCache();
        assertTrue(updated);
        Optional<StandardTokenClaims> validate = iamSecurityFilterConfiguration.getIamClient().validate(new JWToken(projectAdminTokens.getAccessToken()));
        assertTrue(validate.isPresent());
    }

    @Test
    @Order(4)
    void testActuatorEndpoint() throws IOException {
        Request request = new Request.Builder()
                .url(restTemplate.getRootUri() + "/actuator")
                .get()
                .build();
        Response response = httpClient.newCall(request).execute();
        assertEquals(HttpStatus.OK.value(), response.code());
    }

    @Test
    @Order(10)
    void getSystemInfoTest() throws IOException {
        Request request = new Request.Builder()
                .url(restTemplate.getRootUri() + "/api/system/info")
                .get()
                .build();
        Response response = httpClient.newCall(request).execute();
        assertEquals(HttpStatus.OK.value(), response.code());
        SystemInfo<Void> systemInfo = objectMapper.readValue(response.body().string(), new TypeReference<SystemInfo<Void>>(){});
        assertNotNull(systemInfo);
        assertNotNull(systemInfo.getId());
        assertNotNull(systemInfo.getVersion());
        assertNotNull(systemInfo.getName());
        assertEquals("device-controller-001", systemInfo.getId());
        assertNull(systemInfo.getProperties());
    }

    @Test
    @Order(11)
    void checkDevices() throws IOException {
        DeviceInfo[] devices = getDevices();
        assertEquals(0, devices.length);
    }

    @Test
    @Order(12)
    void addDevices() throws IOException {
        DeviceCredentials credentials = new DeviceCredentials("admin", "secret");
        DeviceCreateRequest request = new DeviceCreateRequest("device-01", "device-sim", "http://host:8080/data", credentials, "group-001");
        assertEquals(HttpStatus.OK.value(), createDevice(request));

        credentials = new DeviceCredentials("admin", "secret");
        request = new DeviceCreateRequest("device-02", "device-sim", "http://host:8080/data", credentials, "group-001");
        assertEquals(HttpStatus.OK.value(), createDevice(request));
    }

    @Test
    @Order(13)
    void checkDevicesAgain() throws IOException {
        DeviceInfo[] devices = getDevices();
        assertEquals(2, devices.length);
    }

    @Test
    @Order(14)
    void testQueryDevice() throws IOException {
        DeviceQuery deviceQuery = new DeviceQuery("q-01", "device-01", "system-info", null);
        DeviceQueryResponse response = queryDevice(deviceQuery);
        assertNotNull(response);

        SystemInfo<SimInfo> systemInfo = objectMapper.readValue(response.payload().traverse(), new TypeReference<>(){});
        assertNotNull(systemInfo);
        assertNotNull(systemInfo.getId());
        assertNotNull(systemInfo.getType());
        assertNotNull(systemInfo.getVersion());
        assertEquals("OK", systemInfo.getProperties().getStatus());

        ObjectNode objectNode = objectMapper.valueToTree(new DataRequest("hi"));
        deviceQuery = new DeviceQuery("q-01", "device-01", "data", objectNode);
        response = queryDevice(deviceQuery);
        assertNotNull(response);
        DataResponse dataResponse = objectMapper.treeToValue(response.payload(), DataResponse.class);
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.getMessage());
        assertEquals("hi", dataResponse.getMessage());
    }

    @Test
    @Order(15)
    void deleteDevice() throws IOException {
        assertEquals(HttpStatus.OK.value(), deleteDevice("device-01"));
        DeviceInfo[] devices = getDevices();
        assertEquals(1, devices.length);
    }

    @Test
    @Order(16)
    void deleteLastDevice() throws IOException {
        assertEquals(HttpStatus.OK.value(), deleteDevice("device-02"));
        DeviceInfo[] devices = getDevices();
        assertEquals(0, devices.length);
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.4-bionic");
        static GenericContainer iamServiceContainer = new GenericContainer("jurajveverka/iam-service:2.5.5-RELEASE-amd64")
                .withExposedPorts(8080)
                .withReuse(true);

        private static void startContainers() {
            Startables.deepStart(Stream.of(mongoDBContainer, iamServiceContainer)).join();
        }

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            startContainers();

            Assertions.assertTrue(mongoDBContainer.isRunning());
            Integer boundPort = mongoDBContainer.getMappedPort(DOCKER_EXPOSED_MONGO_PORT);

            LOG.info("MONGO      : {}", mongoDBContainer.getReplicaSetUrl());
            LOG.info("MONGO      : mongodb://localhost:{}/test", boundPort);

            Assertions.assertTrue(iamServiceContainer.isRunning());
            iamServiceBaseURL = "http://" + iamServiceContainer.getContainerIpAddress() + ":" + iamServiceContainer.getMappedPort(IAM_SERVICE_EXPOSED_PORT);
            LOG.info("IAM-SERVICE: {}:{}", iamServiceContainer.getContainerIpAddress(), iamServiceContainer.getMappedPort(IAM_SERVICE_EXPOSED_PORT));
            LOG.info("IAM-SERVICE: {}", iamServiceBaseURL);

            Map appConfig = Map.of(
                    "spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl(),
                    "app.iamClient.baseUrl", iamServiceBaseURL
            );

            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            MapPropertySource testContainers = new MapPropertySource("testcontainers", appConfig);
            environment.getPropertySources().addFirst(testContainers);
        }
    }

    private DeviceInfo[] getDevices() throws IOException {
        Request request = new Request.Builder()
                .url(restTemplate.getRootUri() + "/api/admin/devices")
                .addHeader("Authorization", "Bearer: " + projectAdminTokens.getAccessToken())
                .get()
                .build();
        Response response = httpClient.newCall(request).execute();
        assertEquals(HttpStatus.OK.value(), response.code());
        return objectMapper.readValue(response.body().string(), DeviceInfo[].class);
    }

    private int createDevice(DeviceCreateRequest deviceCreateRequest) throws IOException {
        Request request = new Request.Builder()
                .url(restTemplate.getRootUri() + "/api/admin/devices")
                .addHeader("Authorization", "Bearer: " + projectAdminTokens.getAccessToken())
                .post(RequestBody.create(MediaType.parse("application/json"), objectMapper.writeValueAsString(deviceCreateRequest)))
                .build();
        Response response = httpClient.newCall(request).execute();
        return response.code();
    }

    private int deleteDevice(String deviceId) throws IOException {
        Request request = new Request.Builder()
                .url(restTemplate.getRootUri() + "/api/admin/devices/" + deviceId)
                .addHeader("Authorization", "Bearer: " + projectAdminTokens.getAccessToken())
                .delete()
                .build();
        Response response = httpClient.newCall(request).execute();
        return response.code();
    }

    private DeviceQueryResponse queryDevice(DeviceQuery query) throws IOException {
        Request request = new Request.Builder()
                .url(restTemplate.getRootUri() + "/api/data/devices/query")
                .addHeader("Authorization", "Bearer: " + projectAdminTokens.getAccessToken())
                .post(RequestBody.create(MediaType.parse("application/json"), objectMapper.writeValueAsString(query)))
                .build();
        Response response = httpClient.newCall(request).execute();
        assertEquals(HttpStatus.OK.value(), response.code());
        return objectMapper.readValue(response.body().string(), DeviceQueryResponse.class);
    }

}
