package itx.rpi.powercontroller.services.jobs;

import itx.rpi.powercontroller.dto.TaskId;

import java.util.Collection;
import java.util.Date;

public class Task implements Runnable {

    private final TaskId id;
    private final String jobId;
    private final String jobName;
    private final Collection<Action> actions;
    private final Date submitted;

    private ExecutionStatus status;
    private boolean stopped;
    private Action executedAction;
    private Date started;
    private Long duration;

    public Task(TaskId id, String jobId, String jobName, Collection<Action> actions, Date submitted) {
        this.id = id;
        this.jobId = jobId;
        this.jobName = jobName;
        this.status = ExecutionStatus.WAITING;
        this.actions = actions;
        this.stopped = false;
        this.submitted = submitted;
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

    public Date getSubmitted() {
        return submitted;
    }

    @Override
    public void run() {
        try {
            this.stopped = false;
            this.started = new Date();
            this.status = ExecutionStatus.IN_PROGRESS;
            for (Action action : actions) {
                executedAction = action;
                executedAction.execute();
                if (stopped) {
                    this.duration = new Date().getTime() - this.started.getTime();
                    this.status = ExecutionStatus.ABORTED;
                    return;
                }
            }
            this.duration = new Date().getTime() - this.started.getTime();
            this.status = ExecutionStatus.FINISHED;
        } catch (Exception e) {
            this.duration = new Date().getTime() - this.started.getTime();
            this.status = ExecutionStatus.FAILED;
        }
    }

    public void shutdown() {
        this.stopped = true;
        for (Action action : actions) {
            action.stop();
        }
    }

    public Collection<Action> getActions() {
        return actions;
    }

    public Date getStarted() {
        return started;
    }

    public Long getDuration() {
        return duration;
    }

}
