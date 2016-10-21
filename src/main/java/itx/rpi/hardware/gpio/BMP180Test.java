package itx.rpi.hardware.gpio;

import itx.rpi.hardware.gpio.sensors.BMP180;

public class BMP180Test {

	public static void main(String[] args) throws Exception {
		
		System.out.println("BMP180Test started ...");
		BMP180 bmp180 = new BMP180();
		float temp = bmp180.readTemperature();
		float pres = bmp180.readPressure();
		double alt = bmp180.readAltitude();
        System.out.println("temperature: " + temp + " C");
        System.out.println("pressure   : " + (pres/1000) + " kPa");
        System.out.println("altitude   : " + alt + " m");
		System.out.println("BMP180Test done.");

	}	
}
