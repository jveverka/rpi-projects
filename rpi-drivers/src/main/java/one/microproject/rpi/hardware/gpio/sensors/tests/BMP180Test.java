package one.microproject.rpi.hardware.gpio.sensors.tests;

import com.pi4j.context.Context;
import one.microproject.rpi.hardware.gpio.sensors.sensors.BMP180;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BMP180Test {

	private static final Logger LOG = LoggerFactory.getLogger(BMP180Test.class);

	private BMP180Test() {
	}

	public static void test(Context context) throws Exception {
		try (BMP180 bmp180 = new BMP180(context)) {
			LOG.info("BMP180Test started ...");
			for (int i = 0; i < 10; i++) {
				float temp = bmp180.readTemperature();
				float pres = bmp180.readPressure() / 1000;
				LOG.info("[{}] Temperature: {} C, Pressure: {} kPa", i, String.format("%.3f", temp), String.format("%.3f", pres));
				Thread.sleep(500);
			}
			LOG.info("BMP180Test done.");
		}
	}

}
