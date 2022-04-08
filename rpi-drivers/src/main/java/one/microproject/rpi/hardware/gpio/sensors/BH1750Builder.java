package one.microproject.rpi.hardware.gpio.sensors;

import com.pi4j.context.Context;
import one.microproject.rpi.hardware.gpio.sensors.impl.BH1750Impl;

public class BH1750Builder {

    private Context pi4j;
    private int address = BH1750Impl.ADDRESS;
    private int i2cBus = 1;

    public BH1750Builder context(Context pi4j) {
        this.pi4j = pi4j;
        return this;
    }

    public BH1750Builder address(int address) {
        this.address = address;
        return this;
    }

    public BH1750Builder i2cBus(int i2cBus) {
        this.i2cBus = i2cBus;
        return this;
    }

    public BH1750 build() {
        return new BH1750Impl(pi4j, address, i2cBus);
    }

    public static BH1750Builder get() {
        return new BH1750Builder();
    }

}
