package one.microproject.rpi.hardware.gpio.sensors;

/**
 * PCF8591 device, 4 Channel 8-bit analog to digital converter.
 * https://www.adafruit.com/product/4648
 */
public interface PCF8591 extends I2CDevice {

    /**
     * Get max input voltage.
     * @return max input voltage [Volts].
     */
    double getMaxVoltage();

    /**
     * Get AIN0 Value [Volts].
     * @return AIN0 Value [Volts].
     */
    double getAIn0();

    /**
     * Get AIN1 Value [Volts].
     * @return AIN1 Value [Volts].
     */
    double getAIn1();

    /**
     * Get AIN2 Value [Volts].
     * @return AIN2 Value [Volts].
     */
    double getAIn2();

    /**
     * Get AIN3 Value [Volts].
     * @return AIN3 Value [Volts].
     */
    double getAIn3();

}
