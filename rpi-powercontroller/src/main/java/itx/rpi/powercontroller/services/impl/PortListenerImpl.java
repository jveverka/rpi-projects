package itx.rpi.powercontroller.services.impl;

import itx.rpi.powercontroller.dto.CancelledTaskInfo;
import itx.rpi.powercontroller.dto.JobId;
import itx.rpi.powercontroller.dto.TaskId;
import itx.rpi.powercontroller.services.PortListener;
import itx.rpi.powercontroller.services.TaskManagerService;
import itx.rpi.powercontroller.config.KeyEvent;
import itx.rpi.powercontroller.services.jobs.ExecutionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class PortListenerImpl implements PortListener {

    private static final Logger LOG = LoggerFactory.getLogger(PortListenerImpl.class);

    private final Map<Integer, KeyEvent> keyEvents;

    private TaskManagerService taskManagerService;

    public PortListenerImpl(Map<Integer, KeyEvent> keyEvents) {
        this.keyEvents = keyEvents;
    }

    @Override
    public void setTaskManagerService(TaskManagerService taskManagerService) {
        this.taskManagerService = taskManagerService;
    }

    @Override
    public synchronized StateChangeContext onStateChange(Integer port, Boolean state) {
        LOG.info("onStateChange port={}, state={}", port, state);
        KeyEvent keyEvent = keyEvents.get(port);
        if (keyEvent == null) {
            LOG.error("no KeyEvent registered on port {}", port);
            return StateChangeContext.getEmpty();
        }
        if (Boolean.TRUE.equals(keyEvent.getToggle()) && Boolean.FALSE.equals(state)) {
            LOG.info("KeyEvent toggle={}, only state=false triggers action, action skipped.", keyEvent.getToggle());
            return StateChangeContext.getEmpty();
        }
        if (taskManagerService != null) {
            JobId onJobId = JobId.from(keyEvent.getToggleOnJob());
            Collection<CancelledTaskInfo> cancelledTaskInfos = taskManagerService.cancelTasks(onJobId);
            Optional<CancelledTaskInfo> inProgressTask = cancelledTaskInfos
                    .stream().filter(c -> ExecutionStatus.IN_PROGRESS.equals(c.getStatusBefore())).findFirst();
            if (inProgressTask.isPresent()) {
                LOG.info("Starting toggleOffJob task");
                JobId offJobId = JobId.from(keyEvent.getToggleOffJob());
                Optional<TaskId> offTaskId = taskManagerService.submitTask(offJobId);
                return StateChangeContext.fromOffTaskId(offTaskId);
            } else {
                LOG.info("Starting toggleOnJob task");
                Optional<TaskId> onTaskId = taskManagerService.submitTask(onJobId);
                return StateChangeContext.fromOnTaskId(onTaskId);
            }
        }
        return StateChangeContext.getEmpty();
    }

}
