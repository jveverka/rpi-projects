package one.microproject.rpi.hardware.gpio.sensors;

public interface BMP180 extends I2CDevice {

    /**
     * Expected to return 0x55, the value is fixed and can be used to check whether communication is functioning.
     * @return chip Id.
     */
    int getId();

    void reset();

    float getTemperature();

    float getPressure();

}
