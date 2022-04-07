package one.microproject.rpi.hardware.gpio.sensors;

public interface BME280 extends I2CDevice {

    /**
     * Expected to return 0x60, the value is fixed and can be used to check whether communication is functioning.
     * @return chip Id.
     */
    int getId();

    void reset();

    int getStatus();

    float getTemperature();

    float getPressure();

    float getHumidity();

}
