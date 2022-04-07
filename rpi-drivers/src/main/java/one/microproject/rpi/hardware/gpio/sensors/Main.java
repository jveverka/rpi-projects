package one.microproject.rpi.hardware.gpio.sensors;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import one.microproject.rpi.hardware.gpio.sensors.tests.ADS1115Test;
import one.microproject.rpi.hardware.gpio.sensors.tests.BME280Test;
import one.microproject.rpi.hardware.gpio.sensors.tests.BMP180Test;
import one.microproject.rpi.hardware.gpio.sensors.tests.HTU21DTest;
import one.microproject.rpi.hardware.gpio.sensors.tests.PCF8591Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Set<String> arguments = toSet(args);
        Context context = null;

        if (arguments.contains("ALL") || arguments.contains("ADS1115")) {
            context = lazyConfigInit(context);
            ADS1115Test.test(context);
        }
        if (arguments.contains("ALL") || arguments.contains("BMP180")) {
            context = lazyConfigInit(context);
            BMP180Test.test(context);
        }
        if (arguments.contains("ALL") || arguments.contains("BME280")) {
            context = lazyConfigInit(context);
            BME280Test.test(context);
        }
        if (arguments.contains("ALL") || arguments.contains("HTU21D")) {
            context = lazyConfigInit(context);
            HTU21DTest.test(context);
        }
        if (arguments.contains("ALL") || arguments.contains("PCF8591")) {
            context = lazyConfigInit(context);
            PCF8591Test.test(context);
        }

        if (context == null) {
            LOG.info("No tests triggered !");
            LOG.info("Use arguments: ALL, ADS1115, BMP180, BME280, HTU21D, PCF8591");
        }
    }

    private static Context lazyConfigInit(Context context) {
        if (context == null) {
            context = Pi4J.newAutoContext();
        }
        return context;
    }

    private static Set<String> toSet(String[] args) {
        Set<String> result = new HashSet<>();
        for (int i=0; i<args.length; i++) {
            result.add(args[i]);
        }
        return result;
    }

}
