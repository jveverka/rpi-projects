package one.microproject.rpi.hardware.gpio.sensors.tests;

import com.pi4j.Pi4J;
import one.microproject.rpi.hardware.gpio.sensors.sensors.PCF8591;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PCF8591Test {

    private static final Logger LOG = LoggerFactory.getLogger(PCF8591Test.class);

    public static void main(String[] args) throws Exception {
        LOG.info("PCF8591Test started ...");
        PCF8591 pcf8591 = new PCF8591(Pi4J.newAutoContext());
        for (int i=0; i<10; i++) {
            double aIn0 = pcf8591.readAIn0();
            LOG.info("A0 Voltage: {} V", aIn0);
            Thread.sleep(500);
        }
        LOG.info("PCF8591Test done.");
        pcf8591.close();
    }

}
