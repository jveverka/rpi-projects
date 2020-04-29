package itx.rpi.powercontroller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.rpi.powercontroller.services.jobs.ExecutionStatus;

public class CancelledTaskInfo {

    private final String id;
    private final String jobId;
    private final ExecutionStatus status;

    @JsonCreator
    public CancelledTaskInfo(@JsonProperty("id") String id,
                             @JsonProperty("jobId") String jobId,
                             @JsonProperty("status") ExecutionStatus status) {
        this.id = id;
        this.jobId = jobId;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getJobId() {
        return jobId;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

}
