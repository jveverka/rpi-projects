package one.microproject.rpi.powercontroller.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.rpi.device.dto.SystemInfo;
import one.microproject.rpi.powercontroller.ClientException;
import one.microproject.rpi.powercontroller.PowerControllerApp;
import one.microproject.rpi.powercontroller.PowerControllerClient;
import one.microproject.rpi.powercontroller.PowerControllerClientBuilder;
import one.microproject.rpi.powercontroller.config.Configuration;
import one.microproject.rpi.powercontroller.dto.JobId;
import one.microproject.rpi.powercontroller.dto.JobInfo;
import one.microproject.rpi.powercontroller.dto.Measurements;
import one.microproject.rpi.powercontroller.dto.ControllerInfo;
import one.microproject.rpi.powercontroller.dto.SystemState;
import one.microproject.rpi.powercontroller.dto.TaskFilter;
import one.microproject.rpi.powercontroller.dto.TaskId;
import one.microproject.rpi.powercontroller.dto.TaskInfo;
import one.microproject.rpi.powercontroller.services.PortListener;
import one.microproject.rpi.powercontroller.services.impl.StateChangeContext;
import one.microproject.rpi.powercontroller.dto.ExecutionStatus;
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

    private static PowerControllerApp.Services services;
    private static ExecutorService executorService;
    private static ObjectMapper mapper;
    private static Configuration configuration;
    private static String clientSecret;

    private static PowerControllerClient powerControllerClient;

    @BeforeAll
    public static void init() throws IOException {
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
        SystemInfo<ControllerInfo> systemInfo = powerControllerClient.getSystemInfo();
        assertNotNull(systemInfo);
        assertEquals(configuration.getId(), systemInfo.getId());
        assertNotNull(systemInfo.getVersion());
        assertNotNull(systemInfo.getName());
        assertNotNull(systemInfo.getType());
        assertNotNull(systemInfo.getProperties().getStarted());
        assertTrue(systemInfo.getUptime() > 0);
        assertTrue(systemInfo.getTimestamp() > 0);
        assertNotNull(systemInfo.getProperties().getUptimeDays());
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
    void testPort0OnAndOff() {
        Integer portId = 0;
        assertTrue(powerControllerClient.setPortState(portId, true));
        SystemState systemState = powerControllerClient.getSystemState();
        assertTrue(systemState.getPorts().get(portId));
        assertTrue(powerControllerClient.setPortState(portId, false));
        systemState = powerControllerClient.getSystemState();
        assertFalse(systemState.getPorts().get(portId));
    }

    @Test
    @Order(6)
    void testKillAllJobId() {
        JobId killAllJobId = powerControllerClient.getKillAllJobId();
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
        boolean result = powerControllerClient.cleanTaskQueue();
        assertTrue(result);
        Collection<TaskInfo> taskInfos = powerControllerClient.getAllTasks();
        assertEquals(0, taskInfos.size());
    }

    @Test
    @Order(9)
    void tasksSubmitAndCancelTest() throws IOException {
        Optional<TaskId> taskId = powerControllerClient.submitTask(JobId.from("toggle-on-job-002"));
        assertTrue(taskId.isPresent());

        boolean waitResult = powerControllerClient.waitForTaskStarted(taskId.get());
        assertTrue(waitResult);

        Optional<TaskInfo> taskInfo = filterById(powerControllerClient.getAllTasks(), taskId.get());
        assertTrue(taskInfo.isPresent());
        assertEquals(ExecutionStatus.IN_PROGRESS, taskInfo.get().getStatus());
        assertTrue(powerControllerClient.cancelTask(taskId.get()));

        waitResult = powerControllerClient.waitForTaskTermination(taskId.get());
        assertTrue(waitResult);

        taskInfo = filterById(powerControllerClient.getAllTasks(), taskId.get());
        assertEquals(ExecutionStatus.ABORTED, taskInfo.get().getStatus());

        boolean result = powerControllerClient.cleanTaskQueue();
        assertTrue(result);
    }

    @Test
    @Order(10)
    void tasksSubmitAndFinishTest() throws IOException {
        Optional<TaskId> taskId = powerControllerClient.submitTask(JobId.from("toggle-on-job-001"));
        assertTrue(taskId.isPresent());
        Optional<TaskInfo> taskInfo = filterById(powerControllerClient.getAllTasks(), taskId.get());
        assertTrue(taskInfo.isPresent());

        boolean waitResult = powerControllerClient.waitForTaskTermination(taskId.get());
        assertTrue(waitResult);

        taskInfo = filterById(powerControllerClient.getAllTasks(), taskId.get());
        assertEquals(ExecutionStatus.FINISHED, taskInfo.get().getStatus());

        boolean result = powerControllerClient.cleanTaskQueue();
        assertTrue(result);
    }

    @Test
    @Order(11)
    void tasksSubmitManyAndCancelAll() throws IOException {
        Collection<TaskInfo> taskInfos = powerControllerClient.getAllTasks();
        assertEquals(0, taskInfos.size());

        Optional<TaskId> taskId01 = powerControllerClient.submitTask(JobId.from("toggle-on-job-002"));
        assertTrue(taskId01.isPresent());
        Optional<TaskId> taskId02 = powerControllerClient.submitTask(JobId.from("toggle-on-job-002"));
        assertTrue(taskId02.isPresent());
        Optional<TaskId> taskId03 = powerControllerClient.submitTask(JobId.from("toggle-on-job-002"));
        assertTrue(taskId03.isPresent());

        boolean waitResult = powerControllerClient.waitForTaskStarted(taskId01.get());
        assertTrue(waitResult);
        taskInfos = powerControllerClient.getAllTasks();
        assertEquals(3, taskInfos.size());

        int inProgressCounter = filterByStatus(taskInfos, ExecutionStatus.IN_PROGRESS);
        assertEquals(1, inProgressCounter);

        int waitingCounter = filterByStatus(taskInfos, ExecutionStatus.WAITING);
        assertEquals(2, waitingCounter);

        boolean cancelled = powerControllerClient.cancelAllTasks();
        assertTrue(cancelled);

        waitResult = powerControllerClient.waitForTaskTermination(taskId03.get());
        assertTrue(waitResult);
        taskInfos = powerControllerClient.getAllTasks();
        assertEquals(3, taskInfos.size());

        int abortedCounter = filterByStatus(taskInfos, ExecutionStatus.ABORTED);
        assertEquals(1, abortedCounter);

        int cancelledCounter = filterByStatus(taskInfos, ExecutionStatus.CANCELLED);
        assertEquals(2, cancelledCounter);

        List<ExecutionStatus> statuses = Arrays.asList(ExecutionStatus.FINISHED);
        Collection<TaskInfo> filteredList = powerControllerClient.getTasks(new TaskFilter(statuses));
        assertEquals(0, filteredList.size());

        statuses = Arrays.asList(ExecutionStatus.FINISHED, ExecutionStatus.ABORTED);
        filteredList = powerControllerClient.getTasks(new TaskFilter(statuses));
        assertEquals(1, filteredList.size());

        statuses = Arrays.asList(ExecutionStatus.FINISHED, ExecutionStatus.ABORTED, ExecutionStatus.CANCELLED);
        filteredList = powerControllerClient.getTasks(new TaskFilter(statuses));
        assertEquals(3, filteredList.size());

        boolean result = powerControllerClient.cleanTaskQueue();
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

        boolean termination = powerControllerClient.waitForTaskTermination(taskOnId);
        assertTrue(termination);
        Optional<TaskInfo> taskInfo = filterById(powerControllerClient.getAllTasks(), taskOnId);
        assertTrue(taskInfo.isPresent());
        assertEquals(ExecutionStatus.FINISHED, taskInfo.get().getStatus());

        stateChangeContext = portListener.onStateChange(4, true);
        assertFalse(stateChangeContext.getOffTaskId().isPresent());
        assertTrue(stateChangeContext.getOnTaskId().isPresent());
        taskOnId = stateChangeContext.getOnTaskId().get();

        stateChangeContext = portListener.onStateChange(4, false);
        assertTrue(stateChangeContext.isEmpty());

        termination = powerControllerClient.waitForTaskTermination(taskOnId);
        assertTrue(termination);
        taskInfo = filterById(powerControllerClient.getAllTasks(), taskOnId);
        assertTrue(taskInfo.isPresent());
        assertEquals(ExecutionStatus.FINISHED, taskInfo.get().getStatus());

        boolean result = powerControllerClient.cleanTaskQueue();
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

        boolean started = powerControllerClient.waitForTaskStarted(taskOnId);
        assertTrue(started);
        Optional<TaskInfo> taskInfo = filterById(powerControllerClient.getAllTasks(), taskOnId);
        assertTrue(taskInfo.isPresent());
        assertEquals(ExecutionStatus.IN_PROGRESS, taskInfo.get().getStatus());

        stateChangeContext = portListener.onStateChange(5, true);
        assertTrue(stateChangeContext.getOffTaskId().isPresent());
        assertFalse(stateChangeContext.getOnTaskId().isPresent());
        TaskId taskOffId = stateChangeContext.getOffTaskId().get();

        stateChangeContext = portListener.onStateChange(5, false);
        assertTrue(stateChangeContext.isEmpty());

        boolean termination = powerControllerClient.waitForTaskTermination(taskOffId);
        assertTrue(termination);
        taskInfo = filterById(powerControllerClient.getAllTasks(), taskOffId);
        assertTrue(taskInfo.isPresent());
        assertEquals(ExecutionStatus.FINISHED, taskInfo.get().getStatus());

        boolean result = powerControllerClient.cleanTaskQueue();
        assertTrue(result);
    }

    @AfterAll
    public static void shutdown() throws Exception {
        services.getServer().stop();
        services.shutdown();
        executorService.shutdown();
    }

    private Optional<TaskInfo> filterById(Collection<TaskInfo> taskInfos, TaskId id) {
        for (TaskInfo taskInfo: taskInfos) {
            if (id.getId().equals(taskInfo.getId())) {
                return Optional.of(taskInfo);
            }
        }
        return Optional.empty();
    }

    private int filterByStatus(Collection<TaskInfo> taskInfos, ExecutionStatus executionStatus) {
        int counter = 0;
        for (TaskInfo taskInfo: taskInfos) {
            if (executionStatus.equals(taskInfo.getStatus())) {
                counter++;
            }
        }
        return counter;
    }

}
