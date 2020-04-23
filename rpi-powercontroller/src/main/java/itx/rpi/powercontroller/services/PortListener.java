package itx.rpi.powercontroller.services;

import java.util.EventListener;

public interface PortListener extends EventListener {

    void setTaskManagerService(TaskManagerService taskManagerService);

    void onStateChange(Integer port, Boolean state);

}
