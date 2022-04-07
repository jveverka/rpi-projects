package one.microproject.rpi.hardware.gpio.sensors;

import com.pi4j.context.Context;
import one.microproject.rpi.hardware.gpio.sensors.impl.PCF8591Impl;

public class PCF8591Builder {

    private Context pi4j;
    private int address = PCF8591Impl.ADDRESS;
    private int i2cBus = 1;
    private double maxVoltage = PCF8591Impl.MAX_VOLTAGE;

    public PCF8591Builder context(Context pi4j) {
        this.pi4j = pi4j;
        return this;
    }

    public PCF8591Builder address(int address) {
        this.address = address;
        return this;
    }

    public PCF8591Builder i2cBus(int i2cBus) {
        this.i2cBus = i2cBus;
        return this;
    }

    public PCF8591Builder maxVoltage(double maxVoltage) {
        this.maxVoltage = maxVoltage;
        return this;
    }

    public PCF8591 build() {
        return new PCF8591Impl(pi4j, address, maxVoltage, i2cBus);
    }

    public static PCF8591Builder get() {
        return new PCF8591Builder();
    }

}
