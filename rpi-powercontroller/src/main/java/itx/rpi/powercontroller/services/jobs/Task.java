package itx.rpi.powercontroller.services.jobs;

import itx.rpi.powercontroller.dto.TaskId;

import java.util.Collection;

public class Task implements Runnable {

    private final TaskId id;
    private final String jobId;
    private final String jobName;
    private final Collection<Action> actions;

    private ExecutionStatus status;
    private boolean stopped;
    private Action executedAction;

    public Task(TaskId id, String jobId, String jobName, Collection<Action> actions) {
        this.id = id;
        this.jobId = jobId;
        this.jobName = jobName;
        this.status = ExecutionStatus.WAITING;
        this.actions = actions;
        this.stopped = false;
    }

    public TaskId getId() {
        return id;
    }

    public String getJobId() {
        return jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    @Override
    public void run() {
        try {
            this.stopped = false;
            this.status = ExecutionStatus.IN_PROGRESS;
            for (Action action : actions) {
                executedAction = action;
                executedAction.execute();
                if (stopped) {
                    this.status = ExecutionStatus.ABORTED;
                    return;
                }
            }
            this.status = ExecutionStatus.FINISHED;
        } catch (Exception e) {
            this.status = ExecutionStatus.FAILED;
        }
    }

    public void shutdown() {
        this.stopped = true;
        if (executedAction != null) {
            executedAction.stop();
        }
        this.status = ExecutionStatus.ABORTED;
    }

    public Collection<Action> getActions() {
        return actions;
    }

}
