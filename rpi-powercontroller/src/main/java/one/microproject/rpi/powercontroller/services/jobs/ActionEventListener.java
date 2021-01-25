package one.microproject.rpi.powercontroller.services.jobs;

import one.microproject.rpi.powercontroller.dto.ExecutionStatus;

public interface ActionEventListener {

    void onActionStateChange(Integer ordinal, ExecutionStatus state);

}
