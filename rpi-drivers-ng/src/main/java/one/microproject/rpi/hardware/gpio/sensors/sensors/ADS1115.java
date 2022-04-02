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

    private final I2C i2c;

    public ADS1115(Context pi4j) {
        this(pi4j, ADDRESS);
    }

    public ADS1115(Context pi4j, int address) {
        I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
        I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j).id("ADS1115").bus(1).device(address).build();
        i2c = i2CProvider.create(i2cConfig);
    }

    public double readAIn0() {
        return 0;
    }

    public double readAIn1() {
        return 0;
    }

    public double readAIn2() {
        return 0;
    }

    public double readAIn3() {
        return 0;
    }

    @Override
    public void close() throws Exception {
        i2c.close();
    }

}
