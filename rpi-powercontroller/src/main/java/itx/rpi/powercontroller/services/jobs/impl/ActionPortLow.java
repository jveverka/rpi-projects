package itx.rpi.powercontroller.services.jobs.impl;

import itx.rpi.powercontroller.services.RPiService;
import itx.rpi.powercontroller.services.jobs.Action;
import itx.rpi.powercontroller.services.jobs.ExecutionStatus;

public class ActionPortLow implements Action {

    private final Integer port;
    private final RPiService rPiService;

    private ExecutionStatus executionStatus;

    public ActionPortLow(Integer port, RPiService rPiService) {
        this.port = port;
        this.rPiService = rPiService;
        this.executionStatus = ExecutionStatus.WAITING;
    }

    @Override
    public String getType() {
        return "ActionPortLow";
    }

    @Override
    public String getDescription() {
        return "Switch OFF port " + port;
    }

    @Override
    public ExecutionStatus getStatus() {
        return executionStatus;
    }

    @Override
    public void execute() {
        if (!ExecutionStatus.WAITING.equals(executionStatus)) {
            return;
        }
        this.executionStatus = ExecutionStatus.IN_PROGRESS;
        try {
            rPiService.setPortState(port, false);
        } catch (Exception e) {
            this.executionStatus = ExecutionStatus.FAILED;
        }
        this.executionStatus = ExecutionStatus.FINISHED;
    }

    @Override
    public void stop() {
        if (!ExecutionStatus.FINISHED.equals(executionStatus) && !ExecutionStatus.ABORTED.equals(executionStatus)) {
            this.executionStatus = ExecutionStatus.ABORTED;
        }
    }

}
