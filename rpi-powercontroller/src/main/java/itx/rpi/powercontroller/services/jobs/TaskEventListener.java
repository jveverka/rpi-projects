package itx.rpi.powercontroller.services.jobs;

import itx.rpi.powercontroller.dto.TaskId;

public interface TaskEventListener {

    void onTaskStateChange(TaskId id, ExecutionStatus state);

}
