package one.microproject.rpi.hardware.gpio.sensors;

public interface HTU21DF extends I2CDevice {

    float getTemperature();

    float getHumidity();

}
