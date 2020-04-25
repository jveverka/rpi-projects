package itx.rpi.powercontroller.tests.actions;

import itx.rpi.powercontroller.dto.TaskId;
import itx.rpi.powercontroller.services.jobs.ExecutionStatus;
import itx.rpi.powercontroller.services.jobs.TaskEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TaskEventListenerImpl implements TaskEventListener {

    private final CountDownLatch clWaiting;
    private final CountDownLatch clInProgress;
    private final CountDownLatch clCancelled;
    private final CountDownLatch clFinished;
    private final CountDownLatch clAborted;
    private final CountDownLatch clFailed;
    private final List<ExecutionStatus> states;

    public TaskEventListenerImpl() {
        this.clWaiting = new CountDownLatch(1);
        this.clInProgress = new CountDownLatch(1);
        this.clCancelled = new CountDownLatch(1);
        this.clFinished = new CountDownLatch(1);
        this.clAborted = new CountDownLatch(1);
        this.clFailed = new CountDownLatch(1);
        this.states = new ArrayList<>();
    }

    @Override
    public void onTaskStateChange(TaskId id, ExecutionStatus state) {
        this.states.add(state);
        switch (state) {
            case WAITING:
                clWaiting.countDown();
                break;
            case IN_PROGRESS:
                clInProgress.countDown();
                break;
            case CANCELLED:
                clCancelled.countDown();
                break;
            case FINISHED:
                clFinished.countDown();
                break;
            case ABORTED:
                clAborted.countDown();
                break;
            case FAILED:
                clFailed.countDown();
                break;
            default:
                throw new UnsupportedOperationException("");
        }
    }

    public void waitForWaiting(Long timeout, TimeUnit timeUnit) throws InterruptedException {
        clWaiting.await(timeout, timeUnit);
    }

    public void waitForInProgress(Long timeout, TimeUnit timeUnit) throws InterruptedException {
        clInProgress.await(timeout, timeUnit);
    }

    public void waitForCancelled(Long timeout, TimeUnit timeUnit) throws InterruptedException {
        clCancelled.await(timeout, timeUnit);
    }

    public void waitForFinished(Long timeout, TimeUnit timeUnit) throws InterruptedException {
        clFinished.await(timeout, timeUnit);
    }

    public void waitForAborted(Long timeout, TimeUnit timeUnit) throws InterruptedException {
        clAborted.await(timeout, timeUnit);
    }

    public void waitForFailed(Long timeout, TimeUnit timeUnit) throws InterruptedException {
        clFailed.await(timeout, timeUnit);
    }

    public boolean checkExecutionStateOrder(ExecutionStatus ... states) {
        if (this.states.size() ==  states.length) {
            for (int i = 0; i < states.length; i++) {
                ExecutionStatus executionStatus = this.states.get(i);
                if (!states[i].equals(executionStatus)) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

}
