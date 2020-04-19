package itx.rpi.powercontroller.services.jobs;

import itx.rpi.powercontroller.config.ActionConfiguration;
import itx.rpi.powercontroller.config.actions.ActionPortHighConfig;
import itx.rpi.powercontroller.config.actions.ActionPortLowConfig;
import itx.rpi.powercontroller.config.actions.ActionWaitConfig;
import itx.rpi.powercontroller.dto.ActionTaskInfo;
import itx.rpi.powercontroller.dto.JobId;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskManagerServiceImpl implements TaskManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskManagerServiceImpl.class);

    private final RPiService rPiService;
    private final Map<JobId, Job> jobs;
    private final Map<TaskId, Task> tasks;
    private final ExecutorService executorService;

    public TaskManagerServiceImpl(Collection<Job> jobs, RPiService rPiService) {
        this.rPiService = rPiService;
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
            Task task = new Task(taskId, job.getId(), job.getName(), createActions(job.getActions()));
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
            TaskInfo taskInfo = new TaskInfo(task.getId().getId(), task.getJobId(), task.getJobName(), task.getStatus(), actionTaskInfos, task.getStarted(), task.getDuration());
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
