package itx.rpi.powercontroller.services.impl;

import itx.rpi.powercontroller.dto.TaskId;

import java.util.Optional;

public class StateChangeContext {

    private final Optional<TaskId> onTaskId;
    private final Optional<TaskId> offTaskId;

    public StateChangeContext(Optional<TaskId> onTaskId, Optional<TaskId> offTaskId) {
        this.onTaskId = onTaskId;
        this.offTaskId = offTaskId;
    }

    public Optional<TaskId> getOnTaskId() {
        return onTaskId;
    }

    public Optional<TaskId> getOffTaskId() {
        return offTaskId;
    }

    public boolean isEmpty() {
        return !onTaskId.isPresent() && !offTaskId.isPresent();
    }

    public static final StateChangeContext fromOnTaskId(Optional<TaskId> onTaskId) {
        return new StateChangeContext(onTaskId, Optional.empty());
    }

    public static final StateChangeContext fromOffTaskId(Optional<TaskId> offTaskId) {
        return new StateChangeContext(Optional.empty(), offTaskId);
    }

    public static final StateChangeContext getEmpty() {
        return new StateChangeContext(Optional.empty(), Optional.empty());
    }

}
