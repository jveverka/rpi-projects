package one.microproject.rpi.powercontroller;

import one.microproject.rpi.powercontroller.dto.JobId;
import one.microproject.rpi.powercontroller.dto.JobInfo;
import one.microproject.rpi.powercontroller.dto.Measurements;
import one.microproject.rpi.powercontroller.dto.SetPortRequest;
import one.microproject.rpi.powercontroller.dto.SystemInfo;
import one.microproject.rpi.powercontroller.dto.SystemState;
import one.microproject.rpi.powercontroller.dto.TaskId;
import one.microproject.rpi.powercontroller.dto.TaskInfo;

import java.util.Collection;
import java.util.Optional;

public interface PowerControllerClient {

    SystemInfo getSystemInfo();

    SystemState getSystemState();

    Measurements getMeasurements();

    Collection<JobInfo> getSystemJobs();

    void killAllJobs();

    Collection<TaskInfo> getAllTasks();

    boolean setPortState(SetPortRequest request);

    Optional<TaskId> submitTask(JobId id);

    boolean cancelTask(TaskId id);

    boolean cancelAllTasks();

    boolean waitForTaskStarted(TaskId id);

    boolean waitForTaskTermination(TaskId id);

    boolean cleanTaskQueue();

}
