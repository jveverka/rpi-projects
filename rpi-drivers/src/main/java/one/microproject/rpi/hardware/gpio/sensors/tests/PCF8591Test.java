package one.microproject.rpi.hardware.gpio.sensors.tests;

import com.pi4j.context.Context;
import one.microproject.rpi.hardware.gpio.sensors.PCF8591;
import one.microproject.rpi.hardware.gpio.sensors.PCF8591Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PCF8591Test {

    private static final Logger LOG = LoggerFactory.getLogger(PCF8591Test.class);

    private PCF8591Test() {
    }

    public static void test(Context context) throws Exception {
        try (PCF8591 pcf8591 = PCF8591Builder.get().context(context).build()) {
            LOG.info("PCF8591Test started ...");
            for (int i = 0; i < 10; i++) {
                double aIn0 = pcf8591.getAIn0();
                double aIn1 = pcf8591.getAIn1();
                double aIn2 = pcf8591.getAIn2();
                double aIn3 = pcf8591.getAIn3();
                LOG.info("[{}] Voltages: a0={} V, a1={} V, a2={} V, a3={} V",
                        i, String.format("%.3f", aIn0), String.format("%.3f", aIn1), String.format("%.3f", aIn2), String.format("%.3f", aIn3));
                Thread.sleep(500);
            }
            LOG.info("PCF8591Test done.");
        }
    }

}
