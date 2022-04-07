package one.microproject.rpi.hardware.gpio.sensors.tests;

import com.pi4j.context.Context;
import one.microproject.rpi.hardware.gpio.sensors.BME280;
import one.microproject.rpi.hardware.gpio.sensors.BME280Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BME280Test {

    private static final Logger LOG = LoggerFactory.getLogger(BME280Test.class);

    private BME280Test() {
    }

    public static void test(Context context) throws Exception {
        LOG.info("BME280Test started ...");
        try (BME280 bme280 = BME280Builder.get().context(context).build()) {
            int id = bme280.getId();
            LOG.info("BME280 CHIP ID={}", id);
            int status = bme280.getStatus();
            LOG.info("BME280 status={}", status);
            for (int i = 0; i < 10; i++) {
                float temp = bme280.getTemperature();
                float pres = bme280.getPressure() / 1000;
                float humi = bme280.getHumidity();
                LOG.info("[{}] Temperature: {} C, Pressure: {} kPa, Relative humidity: {} %", i,
                        String.format("%.3f", temp), String.format("%.3f", pres), String.format("%.3f", humi));
                Thread.sleep(500);
            }
        }
        LOG.info("BME280Test done.");
    }

}
