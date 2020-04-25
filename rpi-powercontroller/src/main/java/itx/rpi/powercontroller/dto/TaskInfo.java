package itx.rpi.powercontroller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.rpi.powercontroller.services.jobs.ExecutionStatus;

import java.util.Collection;
import java.util.Date;

public class TaskInfo {

    private final String id;
    private final String jobId;
    private final String jobName;
    private final ExecutionStatus status;
    private final Collection<ActionTaskInfo> actions;
    private final Date submitted;
    private final Date started;
    private final Long duration;

    @JsonCreator
    public TaskInfo(@JsonProperty("id") String id,
                    @JsonProperty("jobId") String jobId,
                    @JsonProperty("jobName") String jobName,
                    @JsonProperty("status") ExecutionStatus status,
                    @JsonProperty("actions") Collection<ActionTaskInfo> actions,
                    @JsonProperty("submitted") Date submitted,
                    @JsonProperty("started") Date started,
                    @JsonProperty("duration") Long duration) {
        this.id = id;
        this.jobId = jobId;
        this.jobName = jobName;
        this.status = status;
        this.actions = actions;
        this.submitted = submitted;
        this.started = started;
        this.duration = duration;
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

    public Date getSubmitted() {
        return submitted;
    }

    public Date getStarted() {
        return started;
    }

    public Long getDuration() {
        return duration;
    }

}
