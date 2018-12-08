package itx.rpi.hardware.gpio.tests;

import itx.rpi.hardware.gpio.sensors.BMP180;
import itx.rpi.hardware.gpio.sensors.HTU21DF;

public class Test implements Runnable {

	private BMP180 bmp180;
	private HTU21DF htu21df;

	public Test() {
		bmp180 = new BMP180();
		htu21df = new HTU21DF();
	}

	@Override
	public void run() {
		System.out.println("Test started ...");

		try {
		float temp1 = bmp180.readTemperature();
		float pres = bmp180.readPressure();
		double alt = bmp180.readAltitude();
        System.out.println("temperature: " + temp1 + " C");
        System.out.println("pressure   : " + (pres/1000) + " kPa");
        System.out.println("altitude   : " + alt + " m");
		float temp2 = htu21df.readTemperature();
		float hum  = htu21df.readHumidity();
        System.out.println("temperature: " + temp2 + " C");
        System.out.println("humidity   : " + hum + " %");
		} catch (Exception e) {
			e.printStackTrace();
		}

        System.out.println("Test done.");
	}

	public static void main(String[] args) throws Exception {
		System.out.println("MAIN started.");
		Test test = new Test();
		Thread thread = new Thread(test);
		thread.run();
		thread.join();
		System.out.println("MAIN done.");
	}

}
