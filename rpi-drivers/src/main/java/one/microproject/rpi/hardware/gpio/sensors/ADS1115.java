package one.microproject.rpi.hardware.gpio.sensors;

public interface ADS1115 extends I2CDevice {

    double getAIn0();

    double getAIn1();

    double getAIn2();

    double getAIn3();

}
