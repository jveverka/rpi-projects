package one.microproject.rpi.hardware.gpio.sensors.tests;

import com.pi4j.context.Context;
import one.microproject.rpi.hardware.gpio.sensors.sensors.HTU21DF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTU21DTest {

	private static final Logger LOG = LoggerFactory.getLogger(HTU21DTest.class);

	private HTU21DTest() {
	}

	public static void test(Context context) throws Exception {
		try (HTU21DF htu21df = new HTU21DF(context)) {
			LOG.info("HTU21DTest started ...");
			for (int i = 0; i < 10; i++) {
				float temp = htu21df.readTemperature();
				float hum = htu21df.readHumidity();
				LOG.info("[{}] Temperature: {} C, Rel. Humidity: {} %", i, String.format("%.3f", temp), String.format("%.3f", hum));
				Thread.sleep(500);
			}
			LOG.info("HTU21DTest done.");
		}
	}
		
}
