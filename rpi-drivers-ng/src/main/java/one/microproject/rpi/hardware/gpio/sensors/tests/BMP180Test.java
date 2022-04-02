package one.microproject.rpi.hardware.gpio.sensors.tests;

import com.pi4j.Pi4J;
import one.microproject.rpi.hardware.gpio.sensors.sensors.BMP180;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BMP180Test {

	private static final Logger LOG = LoggerFactory.getLogger(BMP180Test.class);

	public static void main(String[] args) throws Exception {
		LOG.info("BMP180Test started ...");
		BMP180 bmp180 = new BMP180(Pi4J.newAutoContext());
		float temp = bmp180.readTemperature();
		float pres = bmp180.readPressure();
		LOG.info("temperature: {} C", temp);
		LOG.info("pressure   : {} kPa", (pres/1000));
		LOG.info("BMP180Test done.");
	}

}
