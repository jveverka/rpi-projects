package one.microproject.rpi.hardware.gpio.sensors;

/**
 * BH1750 Light Sensor.
 * https://www.adafruit.com/product/4681
 */
public interface BH1750 extends I2CDevice {

    /**
     * Get Light intensity.
     * @return - ambient light intensity.
     */
    int getLightIntensity();

}
