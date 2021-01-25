package one.microproject.rpi.hardware.gpio.sensors;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 * HC-SR501 PIR motion sensor / detector
 * This sensor requires 1 GPIO pin in INPUT mode
 * @author gergej
 *
 */
public class HCSR501 {
	
	private GpioPinListenerDigital eventListener;
	private GpioPinDigitalInput gpioTriggerPin;
	
	public HCSR501(int triggerPin) {
		this.eventListener = null;
		GpioController gpio = GpioFactory.getInstance();
		gpioTriggerPin = gpio.provisionDigitalInputPin(RaspiPin.getPinByName("GPIO " + triggerPin), PinPullResistance.PULL_DOWN);
	}
	
	public void start(GpioPinListenerDigital listener) {
		stop();
		GpioController gpio = GpioFactory.getInstance();
		gpio.addListener(listener, gpioTriggerPin);
	}
	
	public void stop() {
		if (eventListener != null) {
			GpioController gpio = GpioFactory.getInstance();
			gpio.removeListener(eventListener, gpioTriggerPin);
		}
	}

}
