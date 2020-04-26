package itx.rpi.powercontroller.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import itx.rpi.powercontroller.PowerControllerApp;
import itx.rpi.powercontroller.config.Configuration;
import itx.rpi.powercontroller.dto.JobId;
import itx.rpi.powercontroller.dto.JobInfo;
import itx.rpi.powercontroller.dto.Measurements;
import itx.rpi.powercontroller.dto.SetPortRequest;
import itx.rpi.powercontroller.dto.SystemInfo;
import itx.rpi.powercontroller.dto.SystemState;
import itx.rpi.powercontroller.dto.TaskId;
import itx.rpi.powercontroller.dto.TaskInfo;
import itx.rpi.powercontroller.handlers.HandlerUtils;
import itx.rpi.powercontroller.services.PortListener;
import itx.rpi.powercontroller.services.jobs.ExecutionStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
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
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PowerControllerTests {

    private static final Logger LOG = LoggerFactory.getLogger(PowerControllerTests.class);

    private static final String BASE_URL = "http://localhost:8080/";
    private static final String CLIENT_ID = "client-001";

    private static CloseableHttpClient httpClient;
    private static PowerControllerApp.Services services;
    private static ExecutorService executorService;
    private static ObjectMapper mapper;
    private static Configuration configuration;
    private static String clientSecret;
    private static JobId killAllJobId;

    @BeforeAll
    public static void init() throws IOException {
        httpClient = HttpClients.createDefault();
        executorService = Executors.newSingleThreadExecutor();
        mapper = new ObjectMapper();
        InputStream is = PowerControllerApp.class.getResourceAsStream("/rpi-configuration.json");
        configuration = mapper.readValue(is, Configuration.class);
        clientSecret = configuration.getCredentials().get(CLIENT_ID);
        services = PowerControllerApp.initialize(mapper, configuration);
        executorService.submit(() -> services.getServer().start());
        for (int i=0; i<10; i++) {
            try {
                LOG.info("Waiting for server: ");
                Thread.sleep(500);
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
        get.addHeader("Authorization", HandlerUtils.createBasicAuthorizationFromCredentials(CLIENT_ID, clientSecret));
        CloseableHttpResponse response = httpClient.execute(get);
        assertEquals(200, response.getStatusLine().getStatusCode());
        SystemInfo systemInfo = mapper.readValue(response.getEntity().getContent(), SystemInfo.class);
        assertNotNull(systemInfo);
        assertEquals(configuration.getId(), systemInfo.getId());
        assertNotNull(systemInfo.getVersion());
        assertNotNull(systemInfo.getName());
        assertNotNull(systemInfo.getType());
        assertNotNull(systemInfo.getStarted());
        assertNotNull(systemInfo.getUptime());
        assertNotNull(systemInfo.getUptimeDays());
    }

    @Test
    @Order(2)
    public void testSystemMeasurements() throws IOException {
        HttpGet get = new HttpGet(BASE_URL + "/system/measurements");
        get.addHeader("Authorization", HandlerUtils.createBasicAuthorizationFromCredentials(CLIENT_ID, clientSecret));
        CloseableHttpResponse response = httpClient.execute(get);
        assertEquals(200, response.getStatusLine().getStatusCode());
        Measurements measurements = mapper.readValue(response.getEntity().getContent(), Measurements.class);
        assertNotNull(measurements);
        assertNotNull(measurements.getTimeStamp());
        assertNotNull(measurements.getPressureUnit());
        assertNotNull(measurements.getRelHumidityUnit());
        assertNotNull(measurements.getTemperatureUnit());
        assertNull(measurements.getPressure());
        assertNull(measurements.getRelHumidity());
        assertNull(measurements.getTemperature());
    }

    @Test
    @Order(3)
    public void testSystemState() throws IOException {
        SystemState systemState = getSystemState();
        assertNotNull(systemState);
        assertNotNull(systemState.getTimeStamp());
        assertNotNull(systemState.getPortTypes());
        assertNotNull(systemState.getPorts());
    }

    @Test
    @Order(4)
    public void testJobs() throws IOException {
        HttpGet get = new HttpGet(BASE_URL + "/system/jobs");
        get.addHeader("Authorization", HandlerUtils.createBasicAuthorizationFromCredentials(CLIENT_ID, clientSecret));
        CloseableHttpResponse response = httpClient.execute(get);
        assertEquals(200, response.getStatusLine().getStatusCode());
        JobInfo[] jobs = mapper.readValue(response.getEntity().getContent(), JobInfo[].class);
        assertNotNull(jobs);
        assertTrue(jobs.length > 0);
    }

    @Test
    @Order(5)
    public void testPort0OnAndOff() throws IOException {
        Integer portId = 0;
        assertTrue(setPortState(portId, true));
        SystemState systemState = getSystemState();
        assertTrue(systemState.getPorts().get(portId));
        assertTrue(setPortState(portId, false));
        systemState = getSystemState();
        assertFalse(systemState.getPorts().get(portId));
    }

    @Test
    @Order(6)
    public void testKillAllJobId() throws IOException {
        HttpGet get = new HttpGet(BASE_URL + "/system/jobs/killalljobid");
        get.addHeader("Authorization", HandlerUtils.createBasicAuthorizationFromCredentials(CLIENT_ID, clientSecret));
        CloseableHttpResponse response = httpClient.execute(get);
        assertEquals(200, response.getStatusLine().getStatusCode());
        killAllJobId = mapper.readValue(response.getEntity().getContent(), JobId.class);
        assertNotNull(killAllJobId);
    }

    @Test
    @Order(7)
    public void getTasksTest() throws IOException {
        TaskInfo[] taskInfos = getTasks();
        assertNotNull(taskInfos);
        assertTrue(taskInfos.length == 1);
    }

    @Test
    @Order(8)
    public void tasksSubmitAndCancelTest() throws IOException, InterruptedException {
        Optional<TaskId> taskId = submitTask(JobId.from("toggle-on-job-002"));
        assertTrue(taskId.isPresent());
        Optional<TaskInfo> taskInfo = filterById(getTasks(), taskId.get());
        assertTrue(taskInfo.isPresent());
        assertEquals(ExecutionStatus.IN_PROGRESS, taskInfo.get().getStatus());
        assertTrue(cancelTask(taskId.get()));
        Thread.sleep(200);
        taskInfo = filterById(getTasks(), taskId.get());
        assertEquals(ExecutionStatus.ABORTED, taskInfo.get().getStatus());
    }

    @Test
    @Order(9)
    public void tasksSubmitAndFinishTest() throws IOException, InterruptedException {
        Optional<TaskId> taskId = submitTask(JobId.from("toggle-on-job-001"));
        assertTrue(taskId.isPresent());
        Optional<TaskInfo> taskInfo = filterById(getTasks(), taskId.get());
        assertTrue(taskInfo.isPresent());
        Thread.sleep(20);
        taskInfo = filterById(getTasks(), taskId.get());
        assertEquals(ExecutionStatus.FINISHED, taskInfo.get().getStatus());
    }

    @Test
    @Order(10)
    public void tasksSubmitManyAndCancelAll() throws IOException, InterruptedException {
        Optional<TaskId> taskId = submitTask(JobId.from("toggle-on-job-002"));
        assertTrue(taskId.isPresent());
        taskId = submitTask(JobId.from("toggle-on-job-002"));
        assertTrue(taskId.isPresent());
        taskId = submitTask(JobId.from("toggle-on-job-002"));
        assertTrue(taskId.isPresent());

        int inProgressCounter = filterByStatus(getTasks(), ExecutionStatus.IN_PROGRESS);
        assertEquals(1, inProgressCounter);

        int waitingCounter = filterByStatus(getTasks(), ExecutionStatus.WAITING);
        assertEquals(2, waitingCounter);

        boolean cancelled = cancelAllTasks();
        assertTrue(cancelled);

        Thread.sleep(200);

        int abortedCounter = filterByStatus(getTasks(), ExecutionStatus.ABORTED);
        assertEquals(2, abortedCounter);
    }

    @Test
    @Order(11)
    public void taskTestKeyEventsToggle() throws IOException, InterruptedException {
        PortListener portListener = services.getPortListener();
        portListener.onStateChange(4, true);
        portListener.onStateChange(4, false);
        Thread.sleep(100);
        SystemState state = getSystemState();
        assertTrue(state.getPorts().get(1));
        portListener.onStateChange(4, true);
        portListener.onStateChange(4, false);
        Thread.sleep(100);
        state = getSystemState();
        assertFalse(state.getPorts().get(1));
    }

    @Test
    @Order(12)
    public void taskTestKeyEventsToggleKillAll() throws IOException, InterruptedException {
        PortListener portListener = services.getPortListener();
        portListener.onStateChange(7, true);
        portListener.onStateChange(7, false);
        Thread.sleep(100);
        SystemState state = getSystemState();
        assertTrue(state.getPorts().get(1));
        portListener.onStateChange(7, true);
        portListener.onStateChange(7, false);
        Thread.sleep(100);
        state = getSystemState();
        assertFalse(state.getPorts().get(1));
    }

    @Test
    @Order(13)
    public void taskTestKeyEvents() throws IOException, InterruptedException {
        PortListener portListener = services.getPortListener();
        portListener.onStateChange(6, true);
        Thread.sleep(100);
        SystemState state = getSystemState();
        assertTrue(state.getPorts().get(1));
        portListener.onStateChange(6, false);
        Thread.sleep(100);
        state = getSystemState();
        assertFalse(state.getPorts().get(1));
    }

    @AfterAll
    public static void shutdown() throws Exception {
        httpClient.close();
        services.getServer().stop();
        services.shutdown();
        executorService.shutdown();
    }

    private SystemState getSystemState() throws IOException {
        HttpGet get = new HttpGet(BASE_URL + "/system/state");
        get.addHeader("Authorization", HandlerUtils.createBasicAuthorizationFromCredentials(CLIENT_ID, clientSecret));
        CloseableHttpResponse response = httpClient.execute(get);
        assertEquals(200, response.getStatusLine().getStatusCode());
        return mapper.readValue(response.getEntity().getContent(), SystemState.class);
    }

    private boolean setPortState(Integer port, Boolean state) throws IOException {
        HttpPut put = new HttpPut(BASE_URL + "/system/port");
        SetPortRequest  request = new SetPortRequest(port, state);
        put.addHeader("Authorization", HandlerUtils.createBasicAuthorizationFromCredentials(CLIENT_ID, clientSecret));
        StringEntity stringEntity = new StringEntity(mapper.writeValueAsString(request));
        stringEntity.setContentType("application/json");
        put.setEntity(stringEntity);
        CloseableHttpResponse response = httpClient.execute(put);
        return 200 == response.getStatusLine().getStatusCode();
    }

    private TaskInfo[] getTasks() throws IOException {
        HttpGet get = new HttpGet(BASE_URL + "/system/tasks");
        get.addHeader("Authorization", HandlerUtils.createBasicAuthorizationFromCredentials(CLIENT_ID, clientSecret));
        CloseableHttpResponse response = httpClient.execute(get);
        assertEquals(200, response.getStatusLine().getStatusCode());
        return mapper.readValue(response.getEntity().getContent(), TaskInfo[].class);
    }

    private Optional<TaskId> submitTask(JobId id) throws IOException {
        HttpPut put = new HttpPut(BASE_URL + "/system/tasks/submit");
        put.addHeader("Authorization", HandlerUtils.createBasicAuthorizationFromCredentials(CLIENT_ID, clientSecret));
        StringEntity stringEntity = new StringEntity(mapper.writeValueAsString(id));
        stringEntity.setContentType("application/json");
        put.setEntity(stringEntity);
        CloseableHttpResponse response = httpClient.execute(put);
        if (response.getStatusLine().getStatusCode() == 200) {
            TaskId taskId = mapper.readValue(response.getEntity().getContent(), TaskId.class);
            return Optional.of(taskId);
        } else {
            return Optional.empty();
        }
    }

    private boolean cancelTask(TaskId id) throws IOException {
        HttpPut put = new HttpPut(BASE_URL + "/system/tasks/cancel");
        put.addHeader("Authorization", HandlerUtils.createBasicAuthorizationFromCredentials(CLIENT_ID, clientSecret));
        StringEntity stringEntity = new StringEntity(mapper.writeValueAsString(id));
        stringEntity.setContentType("application/json");
        put.setEntity(stringEntity);
        CloseableHttpResponse response = httpClient.execute(put);
        if (response.getStatusLine().getStatusCode() == 200) {
            return true;
        } else {
            return false;
        }
    }

    private boolean cancelAllTasks() throws IOException {
        HttpPut put = new HttpPut(BASE_URL + "/system/tasks/cancel/all");
        put.addHeader("Authorization", HandlerUtils.createBasicAuthorizationFromCredentials(CLIENT_ID, clientSecret));
        CloseableHttpResponse response = httpClient.execute(put);
        if (response.getStatusLine().getStatusCode() == 200) {
            return true;
        } else {
            return false;
        }
    }

    private Optional<TaskInfo> filterById(TaskInfo[] taskInfos, TaskId id) {
        for (TaskInfo taskInfo: taskInfos) {
            if (id.getId().equals(taskInfo.getId())) {
                return Optional.of(taskInfo);
            }
        }
        return Optional.empty();
    }

    private int filterByStatus(TaskInfo[] taskInfos, ExecutionStatus executionStatus) {
        int counter = 0;
        for (TaskInfo taskInfo: taskInfos) {
            if (executionStatus.equals(taskInfo.getStatus())) {
                counter++;
            }
        }
        return counter;
    }

}
