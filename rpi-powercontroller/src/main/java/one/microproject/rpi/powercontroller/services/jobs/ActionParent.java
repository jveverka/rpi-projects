package one.microproject.rpi.powercontroller.services.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class ActionParent implements Action {

    private static final Logger LOG = LoggerFactory.getLogger(ActionParent.class);

    private final Integer ordinal;
    private final ActionEventListener listener;
    private final CountDownLatch clTermination;
    private final CountDownLatch clStarted;

    private ExecutionStatus status;
    private boolean stopped;
    private Date started;
    private Long duration;

    public ActionParent(Integer ordinal) {
        this.ordinal = ordinal;
        this.status = ExecutionStatus.WAITING;
        this.stopped = false;
        this.listener = (ordinal1, state) -> LOG.info("onActionStateChange ordinal={} {}", ordinal1, state);
        this.clTermination = new CountDownLatch(1);
        this.clStarted = new CountDownLatch(1);
        this.listener.onActionStateChange(ordinal, this.status);
    }

    public ActionParent(Integer ordinal, ActionEventListener listener) {
        this.ordinal = ordinal;
        this.status = ExecutionStatus.WAITING;
        this.stopped = false;
        this.listener = listener;
        this.clTermination = new CountDownLatch(1);
        this.clStarted = new CountDownLatch(1);
        this.listener.onActionStateChange(ordinal, this.status);
    }

    @Override
    public Integer getOrdinal() {
        return ordinal;
    }

    @Override
    public ExecutionStatus getStatus() {
        return status;
    }

    @Override
    public Date getStarted() {
        return started;
    }

    @Override
    public Long getDuration() {
        return duration;
    }

    @Override
    public boolean awaitForStarted(long timeout, TimeUnit duration) throws InterruptedException {
        return clStarted.await(timeout, duration);
    }

    @Override
    public boolean awaitForTermination(long timeout, TimeUnit duration) throws InterruptedException {
        return clTermination.await(timeout, duration);
    }

    @Override
    public void execute() throws Exception {
        if (!ExecutionStatus.WAITING.equals(status)) {
            return;
        }
        this.stopped = false;
        this.started = new Date();
        setExecutionStatus(ExecutionStatus.IN_PROGRESS);
        try {
            taskBody();
            if (isStopped()) {
                setExecutionStatus(ExecutionStatus.ABORTED);
            } else {
                setExecutionStatus(ExecutionStatus.FINISHED);
            }
        } catch (Exception e) {
            setExecutionStatus(ExecutionStatus.FAILED);
            throw e;
        }
    }

    @Override
    public void shutdown() {
        if (ExecutionStatus.WAITING.equals(status)) {
            setExecutionStatus(ExecutionStatus.CANCELLED);
        } else {
            setExecutionStatus(ExecutionStatus.ABORTED);
        }
    }

    protected boolean isStopped() {
        return stopped;
    }

    protected abstract void taskBody() throws Exception;

    private synchronized void setExecutionStatus(ExecutionStatus targetStatus) {
        boolean stateChanged = false;
        if (ExecutionStatus.WAITING.equals(status) && ExecutionStatus.IN_PROGRESS.equals(targetStatus)) {
            this.status = ExecutionStatus.IN_PROGRESS;
            stateChanged = true;
            clStarted.countDown();
        } else if (ExecutionStatus.WAITING.equals(status) && ExecutionStatus.CANCELLED.equals(targetStatus)) {
            this.status = ExecutionStatus.CANCELLED;
            stateChanged = true;
        } else if (ExecutionStatus.IN_PROGRESS.equals(status) && ExecutionStatus.FAILED.equals(targetStatus)) {
            this.status = ExecutionStatus.FAILED;
            stateChanged = true;
        } else if (ExecutionStatus.IN_PROGRESS.equals(status) && ExecutionStatus.FINISHED.equals(targetStatus)) {
            this.status = ExecutionStatus.FINISHED;
            stateChanged = true;
        } else if (ExecutionStatus.IN_PROGRESS.equals(status) && ExecutionStatus.ABORTED.equals(targetStatus)) {
            this.status = ExecutionStatus.ABORTED;
            stateChanged = true;
        }
        if (stateChanged && ExecutionStatus.isTerminalExecutionState(status)) {
            this.stopped = true;
            if (this.started != null) {
                this.duration = new Date().getTime() - this.started.getTime();
            } else {
                this.duration = 0L;
            }
            clTermination.countDown();
        }
        if (stateChanged) {
            this.listener.onActionStateChange(ordinal, targetStatus);
        }
    }


}
