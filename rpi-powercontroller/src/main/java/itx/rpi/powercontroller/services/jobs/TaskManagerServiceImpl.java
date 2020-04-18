package itx.rpi.powercontroller.services.jobs;

import itx.rpi.powercontroller.dto.ActionTaskInfo;
import itx.rpi.powercontroller.dto.JobId;
import itx.rpi.powercontroller.dto.TaskId;
import itx.rpi.powercontroller.dto.TaskInfo;
import itx.rpi.powercontroller.services.TaskManagerService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskManagerServiceImpl implements TaskManagerService {

    private final Map<JobId, Job> jobs;
    private final Map<TaskId, Task> tasks;
    private final ExecutorService executorService;

    public TaskManagerServiceImpl(Collection<Job> jobs) {
        this.jobs = new HashMap<>();
        jobs.forEach(j -> this.jobs.put(JobId.from(j.getId()), j));
        this.executorService = Executors.newSingleThreadExecutor();
        this.tasks = new ConcurrentHashMap<>();
    }

    @Override
    public Collection<Job> getJobs() {
        return jobs.values();
    }

    @Override
    public Optional<TaskId> submitTask(JobId jobId) {
        Job job = jobs.get(jobId);
        if (job != null) {
            TaskId taskId = TaskId.from(UUID.randomUUID().toString());
            Task task = new Task(taskId, job.getId(), job.getName(), job.getActions());
            tasks.put(taskId, task);
            executorService.submit(task);
            return Optional.of(taskId);
        }
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
            TaskInfo taskInfo = new TaskInfo(task.getId().getId(), task.getJobId(), task.getJobName(), task.getStatus(), actionTaskInfos);
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }

    @Override
    public boolean cancelTask(TaskId taskId) {
        Task task = tasks.get(taskId);
        if (task != null) {
            task.shutdown();
            return true;
        }
        return false;
    }

    @Override
    public void close() throws Exception {
        this.executorService.shutdown();
    }

}
