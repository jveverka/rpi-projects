package itx.rpi.powercontroller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.rpi.powercontroller.services.jobs.ExecutionStatus;

public class ActionTaskInfo {

    private final String type;
    private final String description;
    private final ExecutionStatus status;

    @JsonCreator
    public ActionTaskInfo(@JsonProperty("type") String type,
                          @JsonProperty("description") String description,
                          @JsonProperty("status") ExecutionStatus status) {
        this.type = type;
        this.description = description;
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

}
