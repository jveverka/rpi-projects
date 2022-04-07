package one.microproject.rpi.hardware.gpio.sensors.impl;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import one.microproject.rpi.hardware.gpio.sensors.PCF8591;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PCF8591Impl implements PCF8591 {

    private static final Logger LOG = LoggerFactory.getLogger(PCF8591Impl.class);

    public static final int ADDRESS = 0x48;
    public static final double MAX_VOLTAGE = 3.3;
    private static final byte RA0 = 0x45;
    private static final byte RA1 = 0x46;
    private static final byte RA2 = 0x47;
    private static final byte RA3 = 0x44;

    private final int address;
    private final String deviceId;
    private final Context context;
    private final int i2cBus;
    private final I2C i2c;
    private final double maxVoltage;

    public PCF8591Impl(Context pi4j) {
        this(pi4j, ADDRESS, MAX_VOLTAGE, 1);
    }

    public PCF8591Impl(Context pi4j, int address) {
        this(pi4j, address, MAX_VOLTAGE, 1);
    }

    public PCF8591Impl(Context pi4j, double maxVoltage) {
        this(pi4j, ADDRESS, maxVoltage, 1);
    }

    public PCF8591Impl(Context pi4j, int address, double maxVoltage, int i2cBus) {
        this.address = address;
        this.deviceId = "PCF8591";
        this.context = pi4j;
        this.i2cBus = i2cBus;
        this.maxVoltage = maxVoltage;
        I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
        I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j).id(deviceId).bus(i2cBus).device(this.address).build();
        i2c = i2CProvider.create(i2cConfig);
        LOG.info("PCF8591 Connected to i2c bus={} address={}, max voltage={}V. OK.", i2cBus, this.address, this.maxVoltage);
    }

    @Override
    public int getAddress() {
        return address;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public int getI2CBus() {
        return i2cBus;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public double getAIn0() {
        return readAIn(RA0);
    }

    @Override
    public double getAIn1() {
        return readAIn(RA1);
    }

    @Override
    public double getAIn2() {
        return readAIn(RA2);
    }

    @Override
    public double getAIn3() {
        return readAIn(RA3);
    }

    private double readAIn(byte register) {
        int value = i2c.readRegister(register);
        double voltage = ( maxVoltage / 255 ) * value;
        LOG.debug("Input {}, raw: {}, {} V", register, value, voltage);
        return voltage;
    }

    @Override
    public void close() throws Exception {
        i2c.close();
    }

}
