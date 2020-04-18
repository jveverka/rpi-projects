package itx.rpi.powercontroller.services.impl;

import itx.rpi.powercontroller.services.PortListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortListenerImpl implements PortListener {

    private static final Logger LOG = LoggerFactory.getLogger(PortListenerImpl.class);

    @Override
    public void onStateChange(Integer port, Boolean state) {
        LOG.info("onStateChange port={}, state={}", port, state);
    }

}
