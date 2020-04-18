package itx.rpi.powercontroller.services;

import itx.rpi.powercontroller.PowerControllerApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownHook extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(ShutdownHook.class);

    private final PowerControllerApp.Services services;

    public ShutdownHook(PowerControllerApp.Services services) {
        this.services = services;
    }

    @Override
    public void run() {
        try {
            LOG.info("shutting down ...");
            services.shutdown();
            LOG.info("done.");
        } catch (Exception e) {
            LOG.error("Exception: ", e);
        }
    }

}
