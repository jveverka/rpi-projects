package itx.rpi.powercontroller.services.jobs;

import itx.rpi.powercontroller.dto.ActionTaskInfo;
import itx.rpi.powercontroller.dto.TaskInfo;
import itx.rpi.powercontroller.services.TaskManagerService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskManagerServiceImpl implements TaskManagerService {

    private final Map<String, Job> jobs;
    private final Map<String, Task> tasks;
    private final ExecutorService executorService;

    public TaskManagerServiceImpl(Collection<Job> jobs) {
        this.jobs = new HashMap<>();
        jobs.forEach(j -> this.jobs.put(j.getId(), j));
        this.executorService = Executors.newSingleThreadExecutor();
        this.tasks = new ConcurrentHashMap<>();
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
        Collection<TaskInfo> taskInfos = new ArrayList<>();
        for (Task task : tasks.values()) {
            Collection<ActionTaskInfo> actionTaskInfos  = new ArrayList<>();
            for(Action action: task.getActions()) {
                ActionTaskInfo actionTaskInfo = new ActionTaskInfo(action.getType(),  action.getDescription(), action.getStatus());
                actionTaskInfos.add(actionTaskInfo);
            }
            TaskInfo taskInfo = new TaskInfo(task.getId(), task.getJobId(), task.getJobName(), task.getStatus(), actionTaskInfos);
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }

    @Override
    public boolean cancelTask(String taskId) {
        return false;
    }

    @Override
    public void close() throws Exception {
        this.executorService.shutdown();
    }

}
