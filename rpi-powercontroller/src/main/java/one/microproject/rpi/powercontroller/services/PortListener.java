package one.microproject.rpi.powercontroller.services;

import one.microproject.rpi.powercontroller.services.impl.StateChangeContext;

import java.util.EventListener;

public interface PortListener extends EventListener {

    void setTaskManagerService(TaskManagerService taskManagerService);

    StateChangeContext onStateChange(Integer port, Boolean state);

}
