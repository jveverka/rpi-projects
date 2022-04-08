package one.microproject.rpi.hardware.gpio.sensors;

/**
 * Bosch BMP180 Temperature Pressure Sensor.
 * https://www.adafruit.com/product/1603
 */
public interface BMP180 extends I2CDevice {

    /**
     * Expected to return 0x55, the value is fixed and can be used to check whether communication is functioning.
     * @return chip Id.
     */
    int getId();

    /**
     * Reset sensor.
     */
    void reset();

    /**
     * Get ambient temperature reading [Celsius].
     * @return - temperature reading [Celsius].
     */
    float getTemperature();

    /**
     * Get ambient pressure reading [Pascal].
     * @return - pressure reading [Pascal].
     */
    float getPressure();

}
