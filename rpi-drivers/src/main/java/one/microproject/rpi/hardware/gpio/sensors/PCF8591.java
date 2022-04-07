package one.microproject.rpi.hardware.gpio.sensors;

public interface PCF8591 extends I2CDevice {

    double getAIn0();

    double getAIn1();

    double getAIn2();

    double getAIn3();

}
