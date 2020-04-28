package itx.rpi.powercontroller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.rpi.powercontroller.services.jobs.ExecutionStatus;

import java.util.List;

public class TaskFilter {

    private final List<ExecutionStatus> statuses;

    @JsonCreator
    public TaskFilter(@JsonProperty("statuses") List<ExecutionStatus> statuses) {
        this.statuses = statuses;
    }

    public List<ExecutionStatus> getStatuses() {
        return statuses;
    }

}
