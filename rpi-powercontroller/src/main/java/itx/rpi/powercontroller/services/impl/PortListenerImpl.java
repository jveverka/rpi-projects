package itx.rpi.powercontroller.services.impl;

import itx.rpi.powercontroller.dto.JobId;
import itx.rpi.powercontroller.dto.TaskId;
import itx.rpi.powercontroller.services.PortListener;
import itx.rpi.powercontroller.services.TaskManagerService;
import itx.rpi.powercontroller.config.KeyEvent;
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
    public synchronized StateChangeContext onStateChange(Integer port, Boolean state) {
        LOG.info("onStateChange port={}, state={}", port, state);
        KeyEvent keyEvent = keyEvents.get(port);
        if (keyEvent == null) {
            LOG.error("no KeyEvent registered on port {}", port);
        } else if (keyEvent.getToggle() && state == false) {
            LOG.info("KeyEvent toggle={}, only state=false triggers action, action skipped.", keyEvent.getToggle());
        } else if (taskManagerService != null) {
            TaskId taskId = tasks.remove(port);
            if (taskId == null) {
                LOG.info("On toggle ON task");
                JobId onJobId = JobId.from(keyEvent.getToggleOnJob());
                Optional<TaskId> onTaskId = taskManagerService.submitTask(onJobId);
                if (onTaskId.isPresent()) {
                    tasks.put(port, onTaskId.get());
                    LOG.info("on toggle on task {}, job {} submitted.", onTaskId.get(), onJobId.getId());
                } else {
                    LOG.error("toggle on, job not submitted.");
                }
            } else {
                LOG.info("On toggle OFF task {}", taskId);
                taskManagerService.cancelTask(taskId);
                JobId offJobId = JobId.from(keyEvent.getToggleOffJob());
                Optional<TaskId> offTaskId = taskManagerService.submitTask(offJobId);
                if (offTaskId.isPresent()) {
                    LOG.info("on toggle off task {}, job {} submitted.", offTaskId.get(), offJobId.getId());
                } else {
                    LOG.error("toggle off, job not submitted.");
                }
            }
        }
        return new StateChangeContext();
    }

}
