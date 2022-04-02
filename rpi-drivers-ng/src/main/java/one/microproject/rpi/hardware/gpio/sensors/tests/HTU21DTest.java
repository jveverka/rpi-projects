package one.microproject.rpi.hardware.gpio.sensors.tests;

import com.pi4j.Pi4J;
import one.microproject.rpi.hardware.gpio.sensors.sensors.HTU21DF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTU21DTest {

	private static final Logger LOG = LoggerFactory.getLogger(HTU21DTest.class);

	public static void main(String[] args) throws Exception {
		LOG.info("HTU21DTest started ...");
		HTU21DF htu21df = new HTU21DF(Pi4J.newAutoContext());
		float temp = htu21df.readTemperature();
		float hum  = htu21df.readHumidity();
		LOG.info("temperature: {} C", temp);
		LOG.info("humidity   : {} %", hum);
		LOG.info("HTU21DTest done.");
	}
		
}
