package one.microproject.rpi.powercontroller.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.rpi.powercontroller.ClientException;
import one.microproject.rpi.powercontroller.PowerControllerApp;
import one.microproject.rpi.powercontroller.PowerControllerClient;
import one.microproject.rpi.powercontroller.PowerControllerClientBuilder;
import one.microproject.rpi.powercontroller.config.Configuration;
import one.microproject.rpi.powercontroller.dto.JobId;
import one.microproject.rpi.powercontroller.dto.JobInfo;
import one.microproject.rpi.powercontroller.dto.Measurements;
import one.microproject.rpi.powercontroller.dto.SetPortRequest;
import one.microproject.rpi.powercontroller.dto.SystemInfo;
import one.microproject.rpi.powercontroller.dto.SystemState;
import one.microproject.rpi.powercontroller.dto.TaskFilter;
import one.microproject.rpi.powercontroller.dto.TaskId;
import one.microproject.rpi.powercontroller.dto.TaskInfo;
import one.microproject.rpi.powercontroller.services.PortListener;
import one.microproject.rpi.powercontroller.services.impl.StateChangeContext;
import one.microproject.rpi.powercontroller.dto.ExecutionStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static one.microproject.rpi.powercontroller.client.PowerControllerClientImpl.createBasicAuthorizationFromCredentials;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PowerControllerTests {

    private static final Logger LOG = LoggerFactory.getLogger(PowerControllerTests.class);

    private static final String BASE_URL = "http://localhost:8080";
    private static final String CLIENT_ID = "client-001";

    private static CloseableHttpClient httpClient;
    private static PowerControllerApp.Services services;
    private static ExecutorService executorService;
    private static ObjectMapper mapper;
    private static Configuration configuration;
    private static String clientSecret;

    private static PowerControllerClient powerControllerClient;

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
        powerControllerClient = PowerControllerClientBuilder.builder()
                .baseUrl(BASE_URL)
                .withCredentials(CLIENT_ID, clientSecret)
                .build();
        for (int i=0; i<10; i++) {
            try {
                LOG.info("Waiting for server {}", BASE_URL);
                Thread.sleep(500);
                powerControllerClient.getSystemInfo();
                LOG.info("Server INITIALIZED !");
                break;
            } catch (ClientException e) {
                LOG.error("Server not running: ", e);
            } catch (InterruptedException e) {
                LOG.error("Exception", e);
            }
        }
    }

    @Test
    @Order(1)
    void testSystemInfo() {
        SystemInfo systemInfo = powerControllerClient.getSystemInfo();
        assertNotNull(systemInfo);
        assertEquals(configuration.getId(), systemInfo.getId());
        assertNotNull(systemInfo.getVersion());
        assertNotNull(systemInfo.getName());
        assertNotNull(systemInfo.getType());
        assertNotNull(systemInfo.getStarted());
        assertTrue(systemInfo.getUptime() > 0);
        assertNotNull(systemInfo.getUptimeDays());
    }

    @Test
    @Order(2)
    void testSystemMeasurements() {
        Measurements measurements = powerControllerClient.getMeasurements();
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
    void testSystemState() {
        SystemState systemState = powerControllerClient.getSystemState();
        assertNotNull(systemState);
        assertNotNull(systemState.getTimeStamp());
        assertNotNull(systemState.getPortTypes());
        assertNotNull(systemState.getPorts());
    }

    @Test
    @Order(4)
    void testGetJobs() {
        Collection<JobInfo> jobs = powerControllerClient.getSystemJobs();
        assertNotNull(jobs);
        assertTrue(jobs.size() > 0);
    }

    @Test
    @Order(5)
    void testPort0OnAndOff() throws IOException {
        Integer portId = 0;
        assertTrue(setPortState(portId, true));
        SystemState systemState = powerControllerClient.getSystemState();
        assertTrue(systemState.getPorts().get(portId));
        assertTrue(setPortState(portId, false));
        systemState = powerControllerClient.getSystemState();
        assertFalse(systemState.getPorts().get(portId));
    }

    @Test
    @Order(6)
    void testKillAllJobId() {
        JobId killAllJobId = powerControllerClient.killAllJobId();
        assertNotNull(killAllJobId);
    }

    @Test
    @Order(7)
    void getAllTasks() {
        Collection<TaskInfo> taskInfos = powerControllerClient.getAllTasks();
        assertNotNull(taskInfos);
        assertEquals(1, taskInfos.size());
        Optional<TaskInfo> first = taskInfos.stream().findFirst();
        assertTrue(first.isPresent());
        assertEquals(ExecutionStatus.FINISHED, first.get().getStatus());
    }

    @Test
    @Order(8)
    void cleanTaskQueueTest() throws IOException {
        boolean result = cleanTaskQueue();
        assertTrue(result);
        TaskInfo[] taskInfos = getTasks();
        assertEquals(0, taskInfos.length);
    }

    @Test
    @Order(9)
    void tasksSubmitAndCancelTest() throws IOException {
        Optional<TaskId> taskId = submitTask(JobId.from("toggle-on-job-002"));
        assertTrue(taskId.isPresent());

        boolean waitResult = waitForTaskStarted(taskId.get());
        assertTrue(waitResult);

        Optional<TaskInfo> taskInfo = filterById(getTasks(), taskId.get());
        assertTrue(taskInfo.isPresent());
        assertEquals(ExecutionStatus.IN_PROGRESS, taskInfo.get().getStatus());
        assertTrue(cancelTask(taskId.get()));

        waitResult = waitForTaskTermination(taskId.get());
        assertTrue(waitResult);

        taskInfo = filterById(getTasks(), taskId.get());
        assertEquals(ExecutionStatus.ABORTED, taskInfo.get().getStatus());

        boolean result = cleanTaskQueue();
        assertTrue(result);
    }

    @Test
    @Order(10)
    void tasksSubmitAndFinishTest() throws IOException {
        Optional<TaskId> taskId = submitTask(JobId.from("toggle-on-job-001"));
        assertTrue(taskId.isPresent());
        Optional<TaskInfo> taskInfo = filterById(getTasks(), taskId.get());
        assertTrue(taskInfo.isPresent());

        boolean waitResult = waitForTaskTermination(taskId.get());
        assertTrue(waitResult);

        taskInfo = filterById(getTasks(), taskId.get());
        assertEquals(ExecutionStatus.FINISHED, taskInfo.get().getStatus());

        boolean result = cleanTaskQueue();
        assertTrue(result);
    }

    @Test
    @Order(11)
    void tasksSubmitManyAndCancelAll() throws IOException {
        TaskInfo[] taskInfos = getTasks();
        assertEquals(0, taskInfos.length);

        Optional<TaskId> taskId01 = submitTask(JobId.from("toggle-on-job-002"));
        assertTrue(taskId01.isPresent());
        Optional<TaskId> taskId02 = submitTask(JobId.from("toggle-on-job-002"));
        assertTrue(taskId02.isPresent());
        Optional<TaskId> taskId03 = submitTask(JobId.from("toggle-on-job-002"));
        assertTrue(taskId03.isPresent());

        boolean waitResult = waitForTaskStarted(taskId01.get());
        assertTrue(waitResult);
        taskInfos = getTasks();
        assertEquals(3, taskInfos.length);

        int inProgressCounter = filterByStatus(taskInfos, ExecutionStatus.IN_PROGRESS);
        assertEquals(1, inProgressCounter);

        int waitingCounter = filterByStatus(taskInfos, ExecutionStatus.WAITING);
        assertEquals(2, waitingCounter);

        boolean cancelled = cancelAllTasks();
        assertTrue(cancelled);

        waitResult = waitForTaskTermination(taskId03.get());
        assertTrue(waitResult);
        taskInfos = getTasks();
        assertEquals(3, taskInfos.length);

        int abortedCounter = filterByStatus(taskInfos, ExecutionStatus.ABORTED);
        assertEquals(1, abortedCounter);

        int cancelledCounter = filterByStatus(taskInfos, ExecutionStatus.CANCELLED);
        assertEquals(2, cancelledCounter);

        List<ExecutionStatus> statuses = Arrays.asList(ExecutionStatus.FINISHED);
        TaskInfo[] filteredList = getTasks(new TaskFilter(statuses));
        assertEquals(0, filteredList.length);

        statuses = Arrays.asList(ExecutionStatus.FINISHED, ExecutionStatus.ABORTED);
        filteredList = getTasks(new TaskFilter(statuses));
        assertEquals(1, filteredList.length);

        statuses = Arrays.asList(ExecutionStatus.FINISHED, ExecutionStatus.ABORTED, ExecutionStatus.CANCELLED);
        filteredList = getTasks(new TaskFilter(statuses));
        assertEquals(3, filteredList.length);

        boolean result = cleanTaskQueue();
        assertTrue(result);
    }

    @Test
    @Order(12)
    void taskTestKeyEventsToggleFastJob() throws IOException {
        PortListener portListener = services.getPortListener();
        StateChangeContext stateChangeContext = portListener.onStateChange(4, true);
        assertFalse(stateChangeContext.getOffTaskId().isPresent());
        assertTrue(stateChangeContext.getOnTaskId().isPresent());
        TaskId taskOnId = stateChangeContext.getOnTaskId().get();

        stateChangeContext = portListener.onStateChange(4, false);
        assertTrue(stateChangeContext.isEmpty());

        boolean termination = waitForTaskTermination(taskOnId);
        assertTrue(termination);
        Optional<TaskInfo> taskInfo = filterById(getTasks(), taskOnId);
        assertTrue(taskInfo.isPresent());
        assertEquals(ExecutionStatus.FINISHED, taskInfo.get().getStatus());

        stateChangeContext = portListener.onStateChange(4, true);
        assertFalse(stateChangeContext.getOffTaskId().isPresent());
        assertTrue(stateChangeContext.getOnTaskId().isPresent());
        taskOnId = stateChangeContext.getOnTaskId().get();

        stateChangeContext = portListener.onStateChange(4, false);
        assertTrue(stateChangeContext.isEmpty());

        termination = waitForTaskTermination(taskOnId);
        assertTrue(termination);
        taskInfo = filterById(getTasks(), taskOnId);
        assertTrue(taskInfo.isPresent());
        assertEquals(ExecutionStatus.FINISHED, taskInfo.get().getStatus());

        boolean result = cleanTaskQueue();
        assertTrue(result);
    }

    @Test
    @Order(13)
    void taskTestKeyEventsToggleSlowJob() throws IOException {
        PortListener portListener = services.getPortListener();
        StateChangeContext stateChangeContext = portListener.onStateChange(5, true);
        assertFalse(stateChangeContext.getOffTaskId().isPresent());
        assertTrue(stateChangeContext.getOnTaskId().isPresent());
        TaskId taskOnId = stateChangeContext.getOnTaskId().get();

        stateChangeContext = portListener.onStateChange(5, false);
        assertTrue(stateChangeContext.isEmpty());

        boolean started = waitForTaskStarted(taskOnId);
        assertTrue(started);
        Optional<TaskInfo> taskInfo = filterById(getTasks(), taskOnId);
        assertTrue(taskInfo.isPresent());
        assertEquals(ExecutionStatus.IN_PROGRESS, taskInfo.get().getStatus());

        stateChangeContext = portListener.onStateChange(5, true);
        assertTrue(stateChangeContext.getOffTaskId().isPresent());
        assertFalse(stateChangeContext.getOnTaskId().isPresent());
        TaskId taskOffId = stateChangeContext.getOffTaskId().get();

        stateChangeContext = portListener.onStateChange(5, false);
        assertTrue(stateChangeContext.isEmpty());

        boolean termination = waitForTaskTermination(taskOffId);
        assertTrue(termination);
        taskInfo = filterById(getTasks(), taskOffId);
        assertTrue(taskInfo.isPresent());
        assertEquals(ExecutionStatus.FINISHED, taskInfo.get().getStatus());

        boolean result = cleanTaskQueue();
        assertTrue(result);
    }

    @AfterAll
    public static void shutdown() throws Exception {
        httpClient.close();
        services.getServer().stop();
        services.shutdown();
        executorService.shutdown();
    }

    private boolean setPortState(Integer port, Boolean state) throws IOException {
        HttpPut put = new HttpPut(BASE_URL + "/system/port");
        SetPortRequest  request = new SetPortRequest(port, state);
        put.addHeader("Authorization", createBasicAuthorizationFromCredentials(CLIENT_ID, clientSecret));
        StringEntity stringEntity = new StringEntity(mapper.writeValueAsString(request));
        stringEntity.setContentType("application/json");
        put.setEntity(stringEntity);
        CloseableHttpResponse response = httpClient.execute(put);
        return 200 == response.getStatusLine().getStatusCode();
    }

    private TaskInfo[] getTasks() throws IOException {
        HttpGet get = new HttpGet(BASE_URL + "/system/tasks");
        get.addHeader("Authorization", createBasicAuthorizationFromCredentials(CLIENT_ID, clientSecret));
        CloseableHttpResponse response = httpClient.execute(get);
        assertEquals(200, response.getStatusLine().getStatusCode());
        return mapper.readValue(response.getEntity().getContent(), TaskInfo[].class);
    }

    private TaskInfo[] getTasks(TaskFilter filter) throws IOException {
        HttpPut put = new HttpPut(BASE_URL + "/system/tasks");
        put.addHeader("Authorization", createBasicAuthorizationFromCredentials(CLIENT_ID, clientSecret));
        StringEntity stringEntity = new StringEntity(mapper.writeValueAsString(filter));
        stringEntity.setContentType("application/json");
        put.setEntity(stringEntity);
        CloseableHttpResponse response = httpClient.execute(put);
        assertEquals(200, response.getStatusLine().getStatusCode());
        return mapper.readValue(response.getEntity().getContent(), TaskInfo[].class);
    }

    private Optional<TaskId> submitTask(JobId id) throws IOException {
        HttpPut put = new HttpPut(BASE_URL + "/system/tasks/submit");
        put.addHeader("Authorization", createBasicAuthorizationFromCredentials(CLIENT_ID, clientSecret));
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
        put.addHeader("Authorization", createBasicAuthorizationFromCredentials(CLIENT_ID, clientSecret));
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
        put.addHeader("Authorization", createBasicAuthorizationFromCredentials(CLIENT_ID, clientSecret));
        CloseableHttpResponse response = httpClient.execute(put);
        if (response.getStatusLine().getStatusCode() == 200) {
            return true;
        } else {
            return false;
        }
    }

    private boolean waitForTaskStarted(TaskId id) throws IOException {
        HttpPut put = new HttpPut(BASE_URL + "/system/tasks/wait/started");
        put.addHeader("Authorization", createBasicAuthorizationFromCredentials(CLIENT_ID, clientSecret));
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

    private boolean waitForTaskTermination(TaskId id) throws IOException {
        HttpPut put = new HttpPut(BASE_URL + "/system/tasks/wait/termination");
        put.addHeader("Authorization", createBasicAuthorizationFromCredentials(CLIENT_ID, clientSecret));
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

    private boolean cleanTaskQueue() throws IOException {
        HttpPut put = new HttpPut(BASE_URL + "/system/tasks/clean");
        put.addHeader("Authorization", createBasicAuthorizationFromCredentials(CLIENT_ID, clientSecret));
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
