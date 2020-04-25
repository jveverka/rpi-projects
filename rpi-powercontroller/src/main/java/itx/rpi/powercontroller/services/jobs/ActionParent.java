package itx.rpi.powercontroller.services.jobs;

public abstract class ActionParent implements Action {

    private ExecutionStatus executionStatus;
    private boolean stopped;

    public ActionParent() {
        executionStatus = ExecutionStatus.WAITING;
        stopped = false;
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
        this.executionStatus = ExecutionStatus.IN_PROGRESS;
        try {
            taskBody();
            if (!isStopped()) {
                this.executionStatus = ExecutionStatus.FINISHED;
            }
        } catch (Exception e) {
            this.executionStatus = ExecutionStatus.FAILED;
            throw e;
        }
    }

    @Override
    public void shutdown() {
        if (ExecutionStatus.IN_PROGRESS.equals(executionStatus)) {
            this.executionStatus = ExecutionStatus.ABORTED;
        } else if (ExecutionStatus.WAITING.equals(executionStatus)) {
            this.executionStatus = ExecutionStatus.CANCELLED;
        }
        stopped = true;
    }

    protected boolean isStopped() {
        return stopped;
    }

    protected abstract void taskBody() throws Exception;

}
