package one.microproject.rpi.hardware.gpio.sensors.sensors;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PCF8591 implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(PCF8591.class);

    public static final int ADDRESS = 0x48;
    private static final double MAX_VOLTAGE = 3.3;
    private static final byte A0 = 0x40;
    private static final byte A1 = 0x41;
    private static final byte A2 = 0x42;
    private static final byte A3 = 0x43;

    private final I2C i2c;
    private final double maxVoltage;
    private final int address;

    public PCF8591(Context pi4j) {
        this(pi4j, ADDRESS, MAX_VOLTAGE);
    }

    public PCF8591(Context pi4j, int address) {
        this(pi4j, address, MAX_VOLTAGE);
    }

    public PCF8591(Context pi4j, double maxVoltage) {
        this(pi4j, ADDRESS, maxVoltage);
    }

    public PCF8591(Context pi4j, int address, double maxVoltage) {
        this.maxVoltage = maxVoltage;
        this.address = address;
        I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
        I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j).id("PCF8591").bus(1).device(this.address).build();
        i2c = i2CProvider.create(i2cConfig);
        LOG.info("PCF8591 Connected to bus {}, max voltage={}V. OK.", this.address, this.maxVoltage);
    }

    public double readAIn0() {
        return readAIn(A0);
    }

    public double readAIn1() {
        return readAIn(A1);
    }

    public double readAIn2() {
        return readAIn(A2);
    }

    public double readAIn3() {
        return readAIn(A3);
    }

    public double readAIn(byte inAddress) {
        i2c.write(inAddress);
        int value = i2c.read();
        double voltage = ( 3.3 / 255 ) * value;
        LOG.debug("Input {}, raw: {}, {} V", inAddress, value, voltage);
        return voltage;
    }

    @Override
    public void close() throws Exception {
        i2c.close();
    }

}
