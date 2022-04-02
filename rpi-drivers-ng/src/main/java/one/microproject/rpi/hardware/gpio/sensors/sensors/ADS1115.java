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
    private static final int CONFIG_REGISTER     = 0x01;
    private static final int LO_THRESH_REGISTER  = 0x02;
    private static final int HI_THRESH_REGISTER  = 0x03;

    private static final int CONFIG_REGISTER_TEMPLATE = 0b1000000110000011;

    public enum GAIN {
        GAIN_6_144V(0b0000000000000000, 187.5/1_000_000),
        GAIN_4_096V(0b0000001000000000, 125.0/1_000_000),
        GAIN_2_048V(0b0000010000000000, 62.5/1_000_000),
        GAIN_1_024V(0b0000011000000000, 31.25/1_000_000),
        GAIN_0_512V(0b0000100000000000, 15.625/1_000_000),
        GAIN_0_256V(0b0000101000000000, 7.8125/1_000_000);
        private final int gain;
        private final double gainPerByte;
        GAIN(int gain, double gainPerByte) {
            this.gain = gain;
            this.gainPerByte = gainPerByte;
        }
        public int gain() {
            return gain;
        }
        public double gainPerByte() {
            return gainPerByte;
        }
    }

    private static final int A0_IN = 0b0100000000000000;
    private static final int A1_IN = 0b0101000000000000;
    private static final int A2_IN = 0b0110000000000000;
    private static final int A3_IN = 0b0111000000000000;

    private final I2C i2c;
    private final GAIN gain;

    public ADS1115(Context pi4j) {
        this(pi4j, ADDRESS, GAIN.GAIN_4_096V);
    }

    public ADS1115(Context pi4j, int address) {
        this(pi4j, address, GAIN.GAIN_4_096V);
    }

    public ADS1115(Context pi4j, int address, GAIN gain) {
        this.gain = gain;
        I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
        I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j).id("ADS1115").bus(1).device(address).build();
        i2c = i2CProvider.create(i2cConfig);
        LOG.info("ADS1115 Connected to bus {}. OK.", address);
    }

    public double readAIn0() {
        return gain.gainPerByte * readIn(calculateConfig(A0_IN));
    }

    public double readAIn1() {
        return  gain.gainPerByte * readIn(calculateConfig(A1_IN));
    }

    public double readAIn2() {
        return  gain.gainPerByte * readIn(calculateConfig(A2_IN));
    }

    public double readAIn3() {
        return  gain.gainPerByte * readIn(calculateConfig(A3_IN));
    }

    private int readIn(int config) {
        i2c.writeRegisterWord(CONFIG_REGISTER, config);
        try {
            Thread.sleep(25);
        } catch (InterruptedException e) {
            LOG.error("Error: ", e);
        }
        int result = i2c.readRegisterWord(CONVERSION_REGISTER);
        LOG.debug("readIn: {}, raw {}", config, result);
        return result;
    }

    private int calculateConfig(int pinId) {
        return CONFIG_REGISTER_TEMPLATE | gain.gain | pinId;
    }

    @Override
    public void close() throws Exception {
        i2c.close();
    }

}
