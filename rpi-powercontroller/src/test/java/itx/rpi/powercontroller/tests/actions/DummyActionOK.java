package itx.rpi.powercontroller.tests.actions;

import itx.rpi.powercontroller.services.jobs.Action;
import itx.rpi.powercontroller.services.jobs.ExecutionStatus;

import java.util.concurrent.TimeUnit;

public class DummyActionOK implements Action {

    private final Long delay;
    private final TimeUnit timeUnit;

    private ExecutionStatus status;

    public DummyActionOK(Long delay, TimeUnit timeUnit) {
        this.status = ExecutionStatus.WAITING;
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    @Override
    public String getType() {
        return "DummyActionOK";
    }

    @Override
    public String getDescription() {
        return "Dummy action always OK.";
    }

    @Override
    public ExecutionStatus getStatus() {
        return status;
    }

    @Override
    public void execute() {
        this.status = ExecutionStatus.IN_PROGRESS;
        try {
            if (delay > 0) {
                Thread.sleep(timeUnit.toMillis(delay));
            }
        } catch (Exception e) {
            this.status = ExecutionStatus.FAILED;
        }
        this.status = ExecutionStatus.FINISHED;
    }

    @Override
    public void stop() {
        this.status = ExecutionStatus.CANCELLED;
    }

}
