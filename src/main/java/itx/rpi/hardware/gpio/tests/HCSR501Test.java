package itx.rpi.hardware.gpio.tests;

import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import itx.rpi.hardware.gpio.sensors.HCSR501;

public class HCSR501Test {

	public static void main(String[] args) throws InterruptedException {
		System.out.println("HC-SR501 PIR motion detection test started ...");
		
		HCSR501 hcsr501 = new HCSR501(12);
		
		GpioPinListenerDigital listener = new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				System.out.println("TrigerPin: " + event.getState().isHigh());
			}
			
		};
		
		hcsr501.start(listener);
		
		// keep program running until user aborts (CTRL-C)
		for (;;) {
			System.out.println("");
			Thread.sleep(2000);
		}
	}
		
}
