package one.microproject.rpi.hardware.gpio.sensors.impl;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import one.microproject.rpi.hardware.gpio.sensors.BH1750;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BH1750Impl implements BH1750 {

    private static final Logger LOG = LoggerFactory.getLogger(BH1750Impl.class);

    public static final int ADDRESS = 0x23;

    private final int address;
    private final String deviceId;
    private final Context context;
    private final int i2cBus;
    private final I2C bh1750;

    public BH1750Impl(Context pi4j) {
        this(pi4j, ADDRESS, 1);
    }

    public BH1750Impl(Context pi4j, int address, int i2cBus) {
        this.address = address;
        this.deviceId = "BH1750";
        this.context = pi4j;
        this.i2cBus = i2cBus;
        I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
        I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j).id(deviceId).bus(i2cBus).device(address).build();
        bh1750 = i2CProvider.create(i2cConfig);
        LOG.info("BH1750 Connected to i2c bus={} address={}. OK.", i2cBus, address);
        init();
    }

    private void init() {
        bh1750.write((byte) 0x10);
    }

    @Override
    public int getLightIntensity() {
        byte[] p = new byte[2];
        bh1750.read(p, 0, 2);
        int msb = p[0] & 0xff;
        int lsb = p[1] & 0xff;
        LOG.debug("Raw data: msb={} lsb={} p0={} p1={}", msb, lsb, p[0], p[1]);
        return (msb << 8) + lsb;
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
    public void close() throws Exception {
        bh1750.close();
    }

}
