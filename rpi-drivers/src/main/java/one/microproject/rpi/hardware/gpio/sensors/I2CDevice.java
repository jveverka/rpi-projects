package one.microproject.rpi.hardware.gpio.sensors;

import com.pi4j.context.Context;

public interface I2CDevice extends AutoCloseable {

    int getAddress();

    Context getContext();

    int getI2CBus();

    String getDeviceId();

}
