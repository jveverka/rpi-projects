package itx.rpi.powercontroller.dto;

import itx.rpi.powercontroller.services.jobs.ExecutionStatus;

public class ActionTaskInfo {

    private final String type;
    private final String description;
    private final ExecutionStatus status;

    public ActionTaskInfo(String type, String description, ExecutionStatus status) {
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
