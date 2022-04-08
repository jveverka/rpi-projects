package one.microproject.rpi.hardware.gpio.sensors.tests;

import com.pi4j.context.Context;
import one.microproject.rpi.hardware.gpio.sensors.BH1750;
import one.microproject.rpi.hardware.gpio.sensors.BH1750Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BH1750Test {

    private static final Logger LOG = LoggerFactory.getLogger(BH1750Test.class);

    private BH1750Test() {
    }

    public static void test(Context context) throws Exception {
        LOG.info("BH1750Test started ...");
        try (BH1750 bme1750 = BH1750Builder.get().context(context).build()) {
            for (int i = 0; i < 10; i++) {
                int intensity = bme1750.getLightIntensity();
                LOG.info("[{}] Light intensity: {} ", i, intensity);
                Thread.sleep(500);
            }
        }
        LOG.info("BH1750Test done.");
    }

}
