package itx.rpi.powercontroller.services;

import itx.rpi.powercontroller.dto.CancelledTaskInfo;
import itx.rpi.powercontroller.dto.JobId;
import itx.rpi.powercontroller.dto.TaskFilter;
import itx.rpi.powercontroller.dto.TaskId;
import itx.rpi.powercontroller.dto.TaskInfo;
import itx.rpi.powercontroller.services.jobs.Job;

import java.util.Collection;
import java.util.Optional;

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

    void kilAllTasks();

    void cleanTaskQueue();

}
