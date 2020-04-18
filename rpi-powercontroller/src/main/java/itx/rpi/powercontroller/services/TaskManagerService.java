package itx.rpi.powercontroller.services;

import itx.rpi.powercontroller.dto.TaskInfo;
import itx.rpi.powercontroller.services.jobs.Job;

import java.util.Collection;
import java.util.Optional;

public interface TaskManagerService extends AutoCloseable {

    Collection<Job> getJobs();

    Optional<TaskInfo> submitTask(String jobId);

    Collection<TaskInfo> getTasks();

    boolean cancelTask(String taskId);

}
