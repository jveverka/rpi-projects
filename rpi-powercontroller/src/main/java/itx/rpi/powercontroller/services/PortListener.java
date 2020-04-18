package itx.rpi.powercontroller.services;

import java.util.EventListener;

public interface PortListener extends EventListener {

    void onStateChange(Integer port, Boolean state);

}
