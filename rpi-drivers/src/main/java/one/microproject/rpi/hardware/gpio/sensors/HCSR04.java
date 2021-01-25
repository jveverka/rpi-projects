package one.microproject.rpi.hardware.gpio.sensors;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.Gpio;

/**
 * HC-SR04 ultrasonic distance sensor
 * This sensor requires 2 GPIO pins 
 *  1. triggerPin pin is used in INPUT mode
 *  2. echoPin pin is used in OUTPUT mode
 * @author gergej
 *
 */
public class HCSR04 {
	
	private static final long MAX_NANO_DELAY = 23000000;
	private static final int LOW = 0; 
	private static final int HIGH = 1; 
	private int triggerPin;
	private int echoPin;
	
	public HCSR04(int triggerPin, int echoPin) throws InterruptedException {
		this.triggerPin = triggerPin;
		this.echoPin = echoPin;
		Gpio.pinMode(triggerPin, Gpio.OUTPUT);
		Gpio.pinMode(echoPin, Gpio.INPUT);
		Gpio.digitalWrite(triggerPin, LOW);
		GpioController gpio = GpioFactory.getInstance();
		GpioPinDigitalOutput tp = gpio.provisionDigitalOutputPin(RaspiPin.getPinByName("GPIO " + triggerPin));
		tp.setState(false);
	}
	
	public double getDistance() {
		int counter = 0;
		Gpio.digitalWrite(triggerPin, LOW);
		Gpio.delayMicroseconds(2);
		Gpio.digitalWrite(triggerPin, HIGH);
		Gpio.delayMicroseconds(10);
		Gpio.digitalWrite(triggerPin, LOW);
		long start = System.nanoTime();
		while (Gpio.digitalRead(echoPin) == LOW && (System.nanoTime() - start < MAX_NANO_DELAY)) {
			counter++;
		}
		long mid = System.nanoTime();
		if (mid - start >= MAX_NANO_DELAY) {
			System.out.println("mid - start: " + (mid - start) + " counter: " + counter);
			return -1; //obstacle too close to detect distance
		}
		while (Gpio.digitalRead(echoPin) == HIGH && (System.nanoTime() - mid < MAX_NANO_DELAY)) {
		}
		long end = System.nanoTime();
		return (end - mid) / 58000D;
	}
	
}
