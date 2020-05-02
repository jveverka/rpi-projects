package itx.rpi.powercontroller.services;

import itx.rpi.powercontroller.config.TaskQueueInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class TaskQueueCleaner implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(TaskQueueCleaner.class);

    private final TaskManagerService taskManagerService;
    private final TaskQueueInterval taskQueueMaxAge;
    private final AtomicInteger counter;

    public TaskQueueCleaner(TaskManagerService taskManagerService, TaskQueueInterval taskQueueMaxAge) {
        this.taskManagerService = taskManagerService;
        this.taskQueueMaxAge = taskQueueMaxAge;
        this.counter = new AtomicInteger(0);
    }

    @Override
    public void run() {
        int cleanedUp = taskManagerService.cleanTaskQueue(taskQueueMaxAge.getDuration(), taskQueueMaxAge.getTimeUnit());
        int c = counter.incrementAndGet();
        LOG.info("Task queue regular cleanup: maxAge={}:{} cleanedUp={}/{}", taskQueueMaxAge.getDuration(), taskQueueMaxAge.getTimeUnit(), c, cleanedUp);
    }

}
