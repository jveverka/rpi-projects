package one.microproject.rpi.powercontroller.services.impl;

import one.microproject.rpi.powercontroller.config.Configuration;
import one.microproject.rpi.powercontroller.services.PortListener;
import one.microproject.rpi.powercontroller.services.RPiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RPiServiceFactory {

    private RPiServiceFactory() {
    }

    private static final Logger LOG = LoggerFactory.getLogger(RPiServiceFactory.class);

    public static RPiService createService(Configuration configuration, PortListener portListener) {
        if (configuration.isHardware()) {
            LOG.info("Creating RPiHardwareService.");
            return new RPiHardwareServiceImpl(portListener, configuration);
        } else {
            LOG.info("Creating RPiSimulatedService.");
            return new RPiSimulatedServiceImpl(portListener, configuration);
        }
    }

}
