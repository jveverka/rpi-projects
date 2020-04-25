package itx.rpi.powercontroller.services;

import itx.rpi.powercontroller.dto.CancelledTaskInfo;
import itx.rpi.powercontroller.dto.JobId;
import itx.rpi.powercontroller.dto.TaskId;
import itx.rpi.powercontroller.dto.TaskInfo;
import itx.rpi.powercontroller.services.jobs.Job;

import java.util.Collection;
import java.util.Optional;

public interface TaskManagerService extends AutoCloseable {

    Collection<Job> getJobs();

    Optional<TaskId> submitTask(JobId jobId);

    Optional<JobId> getKillAllTasksJobId();

    Collection<TaskInfo> getTasks();

    Optional<CancelledTaskInfo> cancelTask(TaskId taskId);

    void kilAllTasks();

}
