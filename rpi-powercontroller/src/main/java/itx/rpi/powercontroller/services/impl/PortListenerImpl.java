package itx.rpi.powercontroller.services.impl;

import itx.rpi.powercontroller.dto.CancelledTaskInfo;
import itx.rpi.powercontroller.dto.JobId;
import itx.rpi.powercontroller.dto.TaskId;
import itx.rpi.powercontroller.services.PortListener;
import itx.rpi.powercontroller.services.TaskManagerService;
import itx.rpi.powercontroller.services.jobs.ExecutionStatus;
import itx.rpi.powercontroller.services.jobs.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PortListenerImpl implements PortListener {

    private static final Logger LOG = LoggerFactory.getLogger(PortListenerImpl.class);

    private final Map<Integer, KeyEvent> keyEvents;
    private final Map<Integer, TaskId> tasks;

    private TaskManagerService taskManagerService;

    public PortListenerImpl(Map<Integer, KeyEvent> keyEvents) {
        this.keyEvents = keyEvents;
        this.tasks = new ConcurrentHashMap<>();
    }

    @Override
    public void setTaskManagerService(TaskManagerService taskManagerService) {
        this.taskManagerService = taskManagerService;
    }

    @Override
    public void onStateChange(Integer port, Boolean state) {
        LOG.info("onStateChange port={}, state={}", port, state);
        KeyEvent keyEvent = keyEvents.get(port);
        if (keyEvent != null && taskManagerService != null) {
            TaskId taskId = tasks.remove(port);
            if (taskId != null) {
                LOG.info("on toggle off task {}", taskId);
                Optional<CancelledTaskInfo> cancelledTaskInfo = taskManagerService.cancelTask(taskId);
                if (cancelledTaskInfo.isPresent()) {
                    if (ExecutionStatus.IN_PROGRESS.equals(cancelledTaskInfo.get().getStatus())) {
                        LOG.info("submitting toggle off cleanup job {}", keyEvent.getToggleOffJob());
                        JobId stopJobId = JobId.from(keyEvent.getToggleOffJob());
                        taskManagerService.submitTask(stopJobId);
                    } else {
                        LOG.info("toggle off cleanup job {} skipped.", keyEvent.getToggleOffJob());
                    }
                }
            } else {
                LOG.info("on toggle on task {}", taskId);
                JobId onJobId = JobId.from(keyEvent.getToggleOnJob());
                Optional<TaskId> newTaskId = taskManagerService.submitTask(onJobId);
                if (newTaskId.isPresent()) {
                    tasks.put(port, newTaskId.get());
                }
            }
        }
    }

}
