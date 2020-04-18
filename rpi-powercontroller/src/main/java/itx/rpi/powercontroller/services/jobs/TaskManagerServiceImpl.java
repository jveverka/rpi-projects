package itx.rpi.powercontroller.services.jobs;

import itx.rpi.powercontroller.dto.TaskInfo;
import itx.rpi.powercontroller.services.TaskManagerService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TaskManagerServiceImpl implements TaskManagerService {

    private final Map<String, Job> jobs;

    public TaskManagerServiceImpl(Collection<Job> jobs) {
        this.jobs = new HashMap<>();
        jobs.forEach(j -> this.jobs.put(j.getId(), j));
    }

    @Override
    public Collection<Job> getJobs() {
        return jobs.values();
    }

    @Override
    public Optional<TaskInfo> submitTask(String jobId) {
        return Optional.empty();
    }

    @Override
    public Collection<TaskInfo> getTasks() {
        return null;
    }

    @Override
    public boolean cancelTask(String taskId) {
        return false;
    }

    @Override
    public void close() throws Exception {

    }

}
