package one.microproject.rpi.hardware.gpio.sensors.tests;

import com.pi4j.context.Context;
import one.microproject.rpi.hardware.gpio.sensors.BMP180;
import one.microproject.rpi.hardware.gpio.sensors.BMP180Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BMP180Test {

	private static final Logger LOG = LoggerFactory.getLogger(BMP180Test.class);

	private BMP180Test() {
	}

	public static void test(Context context) throws Exception {
		try (BMP180 bmp180 = BMP180Builder.get().context(context).build()) {
			LOG.info("BMP180Test started ...");
			int id = bmp180.getId();
			LOG.info("BME280 CHIP ID={}", id);
			for (int i = 0; i < 10; i++) {
				float temp = bmp180.getTemperature();
				float pres = bmp180.getPressure() / 1000;
				LOG.info("[{}] Temperature: {} C, Pressure: {} kPa", i, String.format("%.3f", temp), String.format("%.3f", pres));
				Thread.sleep(500);
			}
			LOG.info("BMP180Test done.");
		}
	}

}
