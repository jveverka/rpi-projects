package itx.rpi.powercontroller.tests.actions;

import itx.rpi.powercontroller.services.jobs.Action;
import itx.rpi.powercontroller.services.jobs.ExecutionStatus;

import java.util.concurrent.TimeUnit;

public class DummyActionFail implements Action {

    private final Long delay;
    private final TimeUnit timeUnit;

    private ExecutionStatus status;

    public DummyActionFail(Long delay, TimeUnit timeUnit) {
        this.status = ExecutionStatus.WAITING;
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    @Override
    public String getType() {
        return "DummyActionFail";
    }

    @Override
    public String getDescription() {
        return "Dummy action which always fails.";
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
            throw new UnsupportedOperationException("Dummy action exception.");
        } catch (Exception e) {
            this.status = ExecutionStatus.FAILED;
        }
    }

    @Override
    public void stop() {
        this.status = ExecutionStatus.CANCELLED;
    }

}
