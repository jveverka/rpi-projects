package one.microproject.rpi.hardware.gpio.sensors.tests;

import com.pi4j.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ADS1115Test {

    private static final Logger LOG = LoggerFactory.getLogger(ADS1115Test.class);

    private ADS1115Test() {
    }

    public static void test(Context context) {
        LOG.info("ADS1115Test started ...");
        LOG.info("ADS1115Test done.");
    }

}
