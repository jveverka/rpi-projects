package one.microproject.rpi.powercontroller.dto;

public enum ExecutionStatus {

    WAITING,
    IN_PROGRESS,
    FINISHED,
    ABORTED,
    FAILED,
    CANCELLED;

    public static boolean isTerminalExecutionState(ExecutionStatus status) {
        return ExecutionStatus.ABORTED.equals(status) || ExecutionStatus.CANCELLED.equals(status) ||
                ExecutionStatus.FAILED.equals(status) ||  ExecutionStatus.FINISHED.equals(status);
    }

}
