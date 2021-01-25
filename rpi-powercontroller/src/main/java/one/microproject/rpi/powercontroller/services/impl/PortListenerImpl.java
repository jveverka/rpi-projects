package one.microproject.rpi.powercontroller.services.impl;

import one.microproject.rpi.powercontroller.config.EventTrigger;
import one.microproject.rpi.powercontroller.dto.CancelledTaskInfo;
import one.microproject.rpi.powercontroller.dto.JobId;
import one.microproject.rpi.powercontroller.dto.TaskId;
import one.microproject.rpi.powercontroller.services.PortListener;
import one.microproject.rpi.powercontroller.services.TaskManagerService;
import one.microproject.rpi.powercontroller.config.KeyEvent;
import one.microproject.rpi.powercontroller.dto.ExecutionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PortListenerImpl implements PortListener {

    private static final Logger LOG = LoggerFactory.getLogger(PortListenerImpl.class);

    private final Map<Integer, KeyEvent> keyEvents;
    private final Map<Integer, Boolean> keyStates;

    private TaskManagerService taskManagerService;

    public PortListenerImpl(Map<Integer, KeyEvent> keyEvents) {
        this.keyEvents = keyEvents;
        this.keyStates = new ConcurrentHashMap<>();
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
        if (EventTrigger.RAISING_EDGE.equals(keyEvent.getTrigger()) && Boolean.FALSE.equals(state)) {
            LOG.info("KeyEvent trigger={}, only state=false triggers action, action skipped.", keyEvent.getTrigger());
            return StateChangeContext.getEmpty();
        }
        if (EventTrigger.FALLING_EDGE.equals(keyEvent.getTrigger()) && Boolean.TRUE.equals(state)) {
            LOG.info("KeyEvent trigger={}, only state=false triggers action, action skipped.", keyEvent.getTrigger());
            return StateChangeContext.getEmpty();
        }
        if (taskManagerService != null) {
            LOG.info("keyEvent port={}, state={} trigger={} type={}", port, state, keyEvent.getType(), keyEvent.getType());
            switch (keyEvent.getType()) {
                case SUBMIT_JOB: {
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
                case TOGGLE_JOBS: {
                    Boolean portState = keyStates.get(port);
                    if (portState == null) {
                        portState = Boolean.FALSE;
                    }
                    portState = !portState;
                    StateChangeContext stateChangeContext = StateChangeContext.getEmpty();
                    if (Boolean.TRUE.equals(portState)) {
                        LOG.info("Starting toggleOnJob task");
                        JobId onJobId = JobId.from(keyEvent.getToggleOnJob());
                        Optional<TaskId> onTaskId = taskManagerService.submitTask(onJobId);
                        stateChangeContext = StateChangeContext.fromOnTaskId(onTaskId);
                    } else {
                        LOG.info("Starting toggleOffJob task");
                        JobId offJobId = JobId.from(keyEvent.getToggleOffJob());
                        Optional<TaskId> offTaskId = taskManagerService.submitTask(offJobId);
                        stateChangeContext = StateChangeContext.fromOffTaskId(offTaskId);
                    }
                    keyStates.put(port, portState);
                    return stateChangeContext;
                }
                default:
                    throw new UnsupportedOperationException("Unsupported job type: " + keyEvent.getType());
            }

        }
        return StateChangeContext.getEmpty();
    }

}
