package one.microproject.rpi.powercontroller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.rpi.powercontroller.services.jobs.ExecutionStatus;

public class CancelledTaskInfo {

    private final String id;
    private final String jobId;
    private final ExecutionStatus statusBefore;
    private final ExecutionStatus statusAfter;

    @JsonCreator
    public CancelledTaskInfo(@JsonProperty("id") String id,
                             @JsonProperty("jobId") String jobId,
                             @JsonProperty("statusBefore") ExecutionStatus statusBefore,
                             @JsonProperty("statusAfter") ExecutionStatus statusAfter) {
        this.id = id;
        this.jobId = jobId;
        this.statusBefore = statusBefore;
        this.statusAfter = statusAfter;
    }

    public String getId() {
        return id;
    }

    public String getJobId() {
        return jobId;
    }

    public ExecutionStatus getStatusBefore() {
        return statusBefore;
    }

    public ExecutionStatus getStatusAfter() {
        return statusAfter;
    }

}
