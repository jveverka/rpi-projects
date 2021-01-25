package one.microproject.rpi.powercontroller.services.jobs;

import one.microproject.rpi.powercontroller.dto.ExecutionStatus;
import one.microproject.rpi.powercontroller.dto.TaskId;

public interface TaskEventListener {

    void onTaskStateChange(TaskId id, ExecutionStatus state);

}
