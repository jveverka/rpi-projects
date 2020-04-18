package itx.rpi.powercontroller.dto;

import itx.rpi.powercontroller.services.jobs.ExecutionStatus;

public class TaskInfo {

    private final String id;
    private final String jobId;
    private final String jobName;
    private final ExecutionStatus status;

    public TaskInfo(String id, String jobId, String jobName, ExecutionStatus status) {
        this.id = id;
        this.jobId = jobId;
        this.jobName = jobName;
        this.status = status;
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

}
