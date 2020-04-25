package itx.rpi.powercontroller.services.jobs.impl;

import itx.rpi.powercontroller.services.jobs.Action;
import itx.rpi.powercontroller.services.jobs.ExecutionStatus;

import java.util.concurrent.TimeUnit;

public class ActionWait implements Action {

    private final Long delay;
    private final TimeUnit timeUnit;

    private ExecutionStatus executionStatus;
    private boolean stopped;

    public ActionWait(Long delay, TimeUnit timeUnit) {
        this.delay = delay;
        this.timeUnit = timeUnit;
        this.executionStatus = ExecutionStatus.WAITING;
        this.stopped = false;
    }

    @Override
    public String getType() {
        return "ActionWait";
    }

    @Override
    public String getDescription() {
        return "Wait for " + delay + " " + timeUnit;
    }

    @Override
    public ExecutionStatus getStatus() {
        return executionStatus;
    }

    @Override
    public void execute() throws Exception {
        if (!ExecutionStatus.WAITING.equals(executionStatus)) {
            return;
        }
        try {
            this.stopped = false;
            this.executionStatus = ExecutionStatus.IN_PROGRESS;
            long milliseconds = timeUnit.toMillis(delay);
            long ticks = milliseconds / 100;
            long reminder = milliseconds - ticks * 100;
            if (ticks <= 0) {
                Thread.sleep(milliseconds);
            } else {
                for (int i = 0; i < ticks; i++) {
                    Thread.sleep(100);
                    if (stopped) {
                        this.executionStatus = ExecutionStatus.FINISHED;
                        return;
                    }
                }
                Thread.sleep(reminder);
            }
            this.executionStatus = ExecutionStatus.FINISHED;
        } catch (Exception e) {
            this.executionStatus = ExecutionStatus.FAILED;
            throw e;
        }
    }

    @Override
    public void stop() {
        this.stopped = true;
        if (!ExecutionStatus.FINISHED.equals(executionStatus) && !ExecutionStatus.ABORTED.equals(executionStatus)) {
            this.executionStatus = ExecutionStatus.ABORTED;
        }
    }

}
