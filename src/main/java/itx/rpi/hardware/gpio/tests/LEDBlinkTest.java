package itx.rpi.hardware.gpio.tests;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;

public class LEDBlinkTest {

	public static void main(String[] args) throws InterruptedException {
		
		final GpioController gpio = GpioFactory.getInstance();
		final GpioPinDigitalOutput led1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
		led1.setState(true);
        Thread.sleep(500);
		led1.setState(false);
        Thread.sleep(500);
		led1.setState(true);
        Thread.sleep(500);
		led1.setState(false);
	}

}
