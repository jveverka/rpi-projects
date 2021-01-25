package one.microproject.rpi.hardware.gpio.tests;

import one.microproject.rpi.hardware.gpio.sensors.HCSR04;

public class HCSR04Test {

	public static void main(String[] args) throws InterruptedException {
		System.out.println("HC-SR04 ultrasonic distance detection test started ...");
		HCSR04 hcsr04 = new HCSR04(10, 11);
		for (int i=0; i<20; i++) {
			System.out.println("distance: " + hcsr04.getDistance() + " cm");
			Thread.sleep(2000);
		}
		System.out.println("done.");
	}
	
}
