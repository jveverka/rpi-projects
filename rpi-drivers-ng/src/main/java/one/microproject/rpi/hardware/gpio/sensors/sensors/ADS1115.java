package one.microproject.rpi.hardware.gpio.sensors.sensors;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ADS1115 implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(ADS1115.class);

    private static final int ADDRESS = 0x48;
    private static final int CONVERSION_REGISTER = 0x00;
    private static final int CONFIG_REGISTER = 0x01;
    private static final int LO_THRESH_REGISTER = 0x02;
    private static final int HI_THRESH_REGISTER = 0x03;

    private static final int CONFIG_REGISTER_VALUE_A0 = 0b1100010110000011;
    private static final int CONFIG_REGISTER_VALUE_A1 = 0b1101010110000011;
    private static final int CONFIG_REGISTER_VALUE_A2 = 0b1110010110000011;
    private static final int CONFIG_REGISTER_VALUE_A3 = 0b1111010110000011;
    private static final double GAIN_PER_BITE = 62.5/1_000_000;

    private final I2C i2c;

    public ADS1115(Context pi4j) {
        this(pi4j, ADDRESS);
    }

    public ADS1115(Context pi4j, int address) {
        I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
        I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j).id("ADS1115").bus(1).device(address).build();
        i2c = i2CProvider.create(i2cConfig);
        LOG.info("ADS1115 Connected to bus {}. OK.", address);
    }

    public double readAIn0() {
        return GAIN_PER_BITE * readIn(CONFIG_REGISTER_VALUE_A0);
    }

    public double readAIn1() {
        return GAIN_PER_BITE * readIn(CONFIG_REGISTER_VALUE_A1);
    }

    public double readAIn2() {
        return GAIN_PER_BITE * readIn(CONFIG_REGISTER_VALUE_A2);
    }

    public double readAIn3() {
        return GAIN_PER_BITE * readIn(CONFIG_REGISTER_VALUE_A3);
    }

    private int readIn(int address) {
        i2c.writeRegisterWord(CONFIG_REGISTER, address);
        try {
            Thread.sleep(25);
        } catch (InterruptedException e) {
            LOG.error("Error: ", e);
        }
        int result = i2c.readRegisterWord(CONVERSION_REGISTER);
        LOG.info("readIn: {}, raw {}", address, result);
        return result;
    }

    @Override
    public void close() throws Exception {
        i2c.close();
    }

}
