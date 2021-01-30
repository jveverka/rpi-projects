package one.microproject.rpi.powercontroller;

import one.microproject.rpi.powercontroller.dto.JobId;
import one.microproject.rpi.powercontroller.dto.JobInfo;
import one.microproject.rpi.powercontroller.dto.Measurements;
import one.microproject.rpi.powercontroller.dto.SystemInfo;
import one.microproject.rpi.powercontroller.dto.SystemState;
import one.microproject.rpi.powercontroller.dto.TaskFilter;
import one.microproject.rpi.powercontroller.dto.TaskInfo;

import java.util.Collection;

/**
 * Read data from RPi Power Controller.
 * https://github.com/jveverka/rpi-projects/tree/master/rpi-powercontroller
 */
public interface PowerControllerReadClient {

    /**
     * Get {@link SystemInfo} for this RPi Power Controller.
     * Contains information like unique ID of this device, type, version and description.
     * @return {@link SystemInfo}
     */
    SystemInfo getSystemInfo();

    /**
     * Get {@link SystemState} for this RPi Power Controller.
     * @return {@link SystemState}
     */
    SystemState getSystemState();

    /**
     * Get sensor(s) {@link Measurements} for this RPi Power Controller.
     * @return {@link Measurements}
     */
    Measurements getMeasurements();

    /**
     * Get {@link JobInfo} for available pre-configured Jobs for this RPi Power Controller.
     * @return Collection of {@link JobInfo}.
     */
    Collection<JobInfo> getSystemJobs();

    /**
     * Get {@link JobId} of 'kill all' job for this RPi Power Controller.
     * @return {@link JobId}
     */
    JobId getKillAllJobId();

    /**
     * Get task queue (waiting, in-progress, finished) tasks.
     * @return Collection of {@link TaskInfo}
     */
    Collection<TaskInfo> getAllTasks();

    /**
     * Get filtered task queue (waiting, in-progress, finished) tasks.
     * @param filter {@link TaskFilter} task filter criteria.
     * @return Collection of {@link TaskInfo}
     */
    Collection<TaskInfo> getTasks(TaskFilter filter);

}
