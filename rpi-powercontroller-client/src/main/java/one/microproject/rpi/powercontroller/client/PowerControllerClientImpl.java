package one.microproject.rpi.powercontroller.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import one.microproject.rpi.powercontroller.PowerControllerClient;
import one.microproject.rpi.powercontroller.dto.JobId;
import one.microproject.rpi.powercontroller.dto.JobInfo;
import one.microproject.rpi.powercontroller.dto.Measurements;
import one.microproject.rpi.powercontroller.dto.SetPortRequest;
import one.microproject.rpi.powercontroller.dto.SystemInfo;
import one.microproject.rpi.powercontroller.dto.SystemState;
import one.microproject.rpi.powercontroller.dto.TaskId;
import one.microproject.rpi.powercontroller.dto.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Optional;

public class PowerControllerClientImpl implements PowerControllerClient {

    private static final Logger LOG = LoggerFactory.getLogger(PowerControllerClientImpl.class);

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public PowerControllerClientImpl() {
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();
    }

    @Override
    public SystemInfo getSystemInfo() {
        return null;
    }

    @Override
    public SystemState getSystemState() {
        return null;
    }

    @Override
    public Measurements getMeasurements() {
        return null;
    }

    @Override
    public Collection<JobInfo> getSystemJobs() {
        return null;
    }

    @Override
    public void killAllJobs() {

    }

    @Override
    public Collection<TaskInfo> getAllTasks() {
        return null;
    }

    @Override
    public boolean setPortState(SetPortRequest request) {
        return false;
    }

    @Override
    public Optional<TaskId> submitTask(JobId id) {
        return Optional.empty();
    }

    @Override
    public boolean cancelTask(TaskId id) {
        return false;
    }

    @Override
    public boolean cancelAllTasks() {
        return false;
    }

    @Override
    public boolean waitForTaskStarted(TaskId id) {
        return false;
    }

    @Override
    public boolean waitForTaskTermination(TaskId id) {
        return false;
    }

    @Override
    public boolean cleanTaskQueue() {
        return false;
    }

}
