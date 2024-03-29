package one.microproject.rpi.hardware.gpio.sensors;

import com.pi4j.context.Context;
import one.microproject.rpi.hardware.gpio.sensors.impl.BMP180Impl;

public class BMP180Builder {

    private Context pi4j;
    private int address = BMP180Impl.ADDRESS;
    private int i2cBus = 1;

    public BMP180Builder context(Context pi4j) {
        this.pi4j = pi4j;
        return this;
    }

    public BMP180Builder address(int address) {
        this.address = address;
        return this;
    }

    public BMP180Builder i2cBus(int i2cBus) {
        this.i2cBus = i2cBus;
        return this;
    }

    public BMP180 build() {
        return new BMP180Impl(pi4j, address, i2cBus);
    }

    public static BMP180Builder get() {
        return new BMP180Builder();
    }

}
