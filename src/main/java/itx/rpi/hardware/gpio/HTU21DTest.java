package itx.rpi.hardware.gpio;

import itx.rpi.hardware.gpio.sensors.HTU21DF;

public class HTU21DTest {

	public static void main(String[] args) throws Exception {
		
		System.out.println("HTU21DTest started ...");
		HTU21DF htu21df = new HTU21DF();
		float temp = htu21df.readTemperature();
		float hum  = htu21df.readHumidity();
        System.out.println("temperature: " + temp + " C");
        System.out.println("humidity   : " + hum + " %");
		System.out.println("HTU21DTest done.");
		
	}
		
}
