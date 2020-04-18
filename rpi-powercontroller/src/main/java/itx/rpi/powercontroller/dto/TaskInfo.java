package itx.rpi.powercontroller.dto;

import itx.rpi.powercontroller.services.jobs.ExecutionStatus;

import java.util.Collection;

public class TaskInfo {

    private final String id;
    private final String jobId;
    private final String jobName;
    private final ExecutionStatus status;
    private final Collection<ActionTaskInfo> actions;

    public TaskInfo(String id, String jobId, String jobName, ExecutionStatus status, Collection<ActionTaskInfo> actions) {
        this.id = id;
        this.jobId = jobId;
        this.jobName = jobName;
        this.status = status;
        this.actions = actions;
    }

    public String getId() {
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

    public Collection<ActionTaskInfo> getActions() {
        return actions;
    }

}
