package itx.rpi.powercontroller.tests.actions;

import itx.rpi.powercontroller.services.jobs.Action;
import itx.rpi.powercontroller.services.jobs.ExecutionStatus;


public class DummyActionFail implements Action {

    private ExecutionStatus status;

    public DummyActionFail() {
        this.status = ExecutionStatus.WAITING;
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
    public void execute() throws Exception {
        this.status = ExecutionStatus.IN_PROGRESS;
        try {
            throw new UnsupportedOperationException("Dummy action exception.");
        } catch (Exception e) {
            this.status = ExecutionStatus.FAILED;
            throw e;
        }
    }

    @Override
    public void stop() {
        this.status = ExecutionStatus.CANCELLED;
    }

}
