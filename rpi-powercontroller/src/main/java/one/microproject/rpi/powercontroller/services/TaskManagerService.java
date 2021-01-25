package one.microproject.rpi.powercontroller.services;

import one.microproject.rpi.powercontroller.dto.CancelledTaskInfo;
import one.microproject.rpi.powercontroller.dto.JobId;
import one.microproject.rpi.powercontroller.dto.TaskFilter;
import one.microproject.rpi.powercontroller.dto.TaskId;
import one.microproject.rpi.powercontroller.dto.TaskInfo;
import one.microproject.rpi.powercontroller.services.jobs.Job;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface TaskManagerService extends AutoCloseable {

    Collection<Job> getJobs();

    Optional<TaskId> submitTask(JobId jobId);

    boolean waitForStarted(TaskId taskId);

    boolean waitForTermination(TaskId taskId);

    Optional<JobId> getKillAllTasksJobId();

    Collection<TaskInfo> getTasks();

    Collection<TaskInfo> getTasks(TaskFilter taskFilter);

    Optional<CancelledTaskInfo> cancelTask(TaskId taskId);

    Collection<CancelledTaskInfo> cancelTasks(JobId jobId);

    int kilAllTasks();

    int cleanTaskQueue();

    int cleanTaskQueue(Long age, TimeUnit timeUnit);

}
