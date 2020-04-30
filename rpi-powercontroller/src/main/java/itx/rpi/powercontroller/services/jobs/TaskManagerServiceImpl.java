package itx.rpi.powercontroller.services.jobs;

import itx.rpi.powercontroller.config.ActionConfiguration;
import itx.rpi.powercontroller.config.actions.ActionPortHighConfig;
import itx.rpi.powercontroller.config.actions.ActionPortLowConfig;
import itx.rpi.powercontroller.config.actions.ActionWaitConfig;
import itx.rpi.powercontroller.dto.ActionTaskInfo;
import itx.rpi.powercontroller.dto.CancelledTaskInfo;
import itx.rpi.powercontroller.dto.JobId;
import itx.rpi.powercontroller.dto.TaskFilter;
import itx.rpi.powercontroller.dto.TaskId;
import itx.rpi.powercontroller.dto.TaskInfo;
import itx.rpi.powercontroller.services.RPiService;
import itx.rpi.powercontroller.services.TaskManagerService;
import itx.rpi.powercontroller.services.jobs.impl.ActionPortHigh;
import itx.rpi.powercontroller.services.jobs.impl.ActionPortLow;
import itx.rpi.powercontroller.services.jobs.impl.ActionWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TaskManagerServiceImpl implements TaskManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskManagerServiceImpl.class);

    private final RPiService rPiService;
    private final String killAllTasksJobId;
    private final Map<JobId, Job> jobs;
    private final Map<TaskId, Task> tasks;

    private ExecutorService executorService;

    public TaskManagerServiceImpl(Collection<Job> jobs, String killAllTasksJobId, RPiService rPiService) {
        this.rPiService = rPiService;
        this.killAllTasksJobId = killAllTasksJobId;
        this.jobs = new HashMap<>();
        jobs.forEach(j -> this.jobs.put(JobId.from(j.getId()), j));
        this.executorService = Executors.newSingleThreadExecutor();
        this.tasks = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized Collection<Job> getJobs() {
        return jobs.values();
    }

    @Override
    public synchronized Optional<TaskId> submitTask(JobId jobId) {
        Job job = jobs.get(jobId);
        if (job != null) {
            TaskId taskId = TaskId.from(UUID.randomUUID().toString());
            if (killAllTasksJobId.equals(job.getId())) {
                try {
                    LOG.info("killing all tasks ...");
                    kilAllTasks();
                    this.executorService.shutdown();
                    this.executorService.awaitTermination(1, TimeUnit.MINUTES);
                    TaskImpl task = new TaskImpl(taskId, JobId.from(job.getId()), job.getName(), createActions(job.getActions()), new Date());
                    tasks.put(taskId, task);
                    task.run();
                } catch (InterruptedException e) {
                    LOG.error("InterruptedException: ", e);
                    Thread.currentThread().interrupt();
                } finally {
                    this.executorService = Executors.newSingleThreadExecutor();
                }
            } else {
                TaskImpl task = new TaskImpl(taskId, JobId.from(job.getId()), job.getName(), createActions(job.getActions()), new Date());
                tasks.put(taskId, task);
                executorService.submit(task);
            }
            return Optional.of(taskId);
        }
        return Optional.empty();
    }

    @Override
    public synchronized Optional<JobId> getKillAllTasksJobId() {
        if (killAllTasksJobId != null) {
            return Optional.of(JobId.from(killAllTasksJobId));
        }
        return Optional.empty();
    }

    @Override
    public synchronized Collection<TaskInfo> getTasks() {
        return getTasks(new TaskFilter(Collections.emptyList()));
    }

    @Override
    public synchronized Collection<TaskInfo> getTasks(TaskFilter taskFilter) {
        Collection<TaskInfo> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            Collection<ActionTaskInfo> actionTaskInfos  = new ArrayList<>();
            for(Action action: task.getActions()) {
                ActionTaskInfo actionTaskInfo = new ActionTaskInfo(action.getType(),  action.getDescription(), action.getStatus());
                actionTaskInfos.add(actionTaskInfo);
            }
            TaskInfo taskInfo = new TaskInfo(task.getId().getId(), task.getJobId().getId(), task.getJobName(), task.getStatus(), actionTaskInfos,
                    task.getSubmitted(), task.getStarted(), task.getDuration());
            result.add(taskInfo);
        }
        if (taskFilter.getStatuses() != null && !taskFilter.getStatuses().isEmpty()) {
            List<ExecutionStatus> acceptedStatuses = taskFilter.getStatuses();
            result = result.stream().filter(t -> acceptedStatuses.contains(t.getStatus())).collect(Collectors.toList());
        }
        return result.stream().sorted(Comparator.comparingLong(t -> t.getSubmitted().getTime())).collect(Collectors.toList());
    }

    @Override
    public synchronized Optional<CancelledTaskInfo> cancelTask(TaskId taskId) {
        Task task = tasks.get(taskId);
        if (task != null) {
            try {
                ExecutionStatus statusBefore = task.getStatus();
                task.shutdown();
                task.await(1, TimeUnit.MINUTES);
                CancelledTaskInfo cancelledTaskInfo = new CancelledTaskInfo(
                        task.getId().getId(), task.getJobId().getId(), statusBefore, task.getStatus()
                );
                return Optional.of(cancelledTaskInfo);
            } catch (Exception e) {
                LOG.error("Cancel Task Exception", e);
            }
        }
        return Optional.empty();
    }

    @Override
    public synchronized Collection<CancelledTaskInfo> cancelTasks(JobId jobId) {
        List<CancelledTaskInfo> result = new ArrayList<>();
        List<Task> filtered = tasks.values().stream()
                .filter(t -> t.getJobId().equals(jobId.getId())).collect(Collectors.toList());
        for (Task task: filtered) {
            try {
                ExecutionStatus statusBefore = task.getStatus();
                if (ExecutionStatus.WAITING.equals(statusBefore)
                        || ExecutionStatus.IN_PROGRESS.equals(statusBefore)) {
                    task.shutdown();
                    task.await(1, TimeUnit.MINUTES);
                    CancelledTaskInfo cancelledTaskInfo = new CancelledTaskInfo(
                            task.getId().getId(), task.getJobId().getId(), statusBefore, task.getStatus()
                    );
                    result.add(cancelledTaskInfo);
                }
            } catch (Exception e) {
                LOG.error("Cancel Task Exception", e);
            }
        }
        return result;
    }

    @Override
    public synchronized void kilAllTasks() {
        for (Task task : tasks.values()) {
            try {
                task.shutdown();
                task.await(1, TimeUnit.MINUTES);
                LOG.info("closed taskId={} jobId={} {}", task.getId(), task.getJobId(), task.getStatus());
            } catch (InterruptedException e) {
                LOG.error("Task closing error: ", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public synchronized void close() throws Exception {
        kilAllTasks();
        this.executorService.shutdown();
    }

    private Collection<Action> createActions(Collection<ActionConfiguration> configs) {
        Collection<Action> actions = new ArrayList<>();
        configs.forEach(c->{
            LOG.info("creating actionType={}", c.getType());
            if (ActionPortHighConfig.class.equals(c.getType())) {
                ActionPortHighConfig config = (ActionPortHighConfig)c;
                ActionPortHigh action = new ActionPortHigh(config.getPort(), rPiService);
                actions.add(action);
            } else if (ActionPortLowConfig.class.equals(c.getType())) {
                ActionPortLowConfig config = (ActionPortLowConfig)c;
                ActionPortLow action = new ActionPortLow(config.getPort(), rPiService);
                actions.add(action);
            } else if (ActionWaitConfig.class.equals(c.getType())) {
                ActionWaitConfig config = (ActionWaitConfig)c;
                ActionWait action = new ActionWait(config.getDelay(), config.getTimeUnitType());
                actions.add(action);
            } else {
                throw new UnsupportedOperationException("Unsupported Action Configuration Type: " + c.getType());
            }
        });
        return actions;
    }

}
