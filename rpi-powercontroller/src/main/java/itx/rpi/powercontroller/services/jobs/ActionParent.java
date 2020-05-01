package itx.rpi.powercontroller.services.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ActionParent implements Action {

    private static final Logger LOG = LoggerFactory.getLogger(ActionParent.class);

    private final Integer ordinal;
    private final ActionEventListener listener;

    private ExecutionStatus executionStatus;
    private boolean stopped;

    public ActionParent(Integer ordinal) {
        this.ordinal = ordinal;
        this.executionStatus = ExecutionStatus.WAITING;
        this.stopped = false;
        this.listener = (ordinal1, state) -> LOG.info("onActionStateChange ordinal={} {}", ordinal1, state);
    }

    public ActionParent(Integer ordinal, ActionEventListener listener) {
        this.ordinal = ordinal;
        this.executionStatus = ExecutionStatus.WAITING;
        this.stopped = false;
        this.listener = listener;
    }

    @Override
    public Integer getOrdinal() {
        return ordinal;
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
