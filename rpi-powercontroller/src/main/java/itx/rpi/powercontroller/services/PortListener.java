package itx.rpi.powercontroller.services;

import itx.rpi.powercontroller.services.impl.StateChangeContext;

import java.util.EventListener;

public interface PortListener extends EventListener {

    void setTaskManagerService(TaskManagerService taskManagerService);

    StateChangeContext onStateChange(Integer port, Boolean state);

}
