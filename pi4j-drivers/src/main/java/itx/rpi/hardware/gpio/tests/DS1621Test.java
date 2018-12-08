package itx.rpi.hardware.gpio.tests;

import itx.rpi.hardware.gpio.sensors.DS1621;

public class DS1621Test {
	
	public static void main(String[] args) throws InterruptedException {
		System.out.println("DS1621 I2C temperature sensor test ...");
		DS1621 ds1621 = new DS1621(0x49);
		for (int i=0; i<10; i++) {
			System.out.println("temperature: " + ds1621.getTemperature() + " celsius");
			Thread.sleep(2000);			
		}
		System.out.println("done.");
	}

}
