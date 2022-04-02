package one.microproject.rpi.hardware.gpio.sensors.sensors;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTU-21D I2C
 * 3.3V temperature and relative humidity sensor
 *
 * @author gergej
 */
public class HTU21DF implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(HTU21DF.class);

    public static final int HTU21DF_ADDRESS = 0x40;
    // HTU21DF Registers
    public static final int HTU21DF_READTEMP = 0xE3;
    public static final int HTU21DF_READHUM = 0xE5;

    public static final int HTU21DF_READTEMP_NH = 0xF3; // NH = no hold
    public static final int HTU21DF_READHUMI_NH = 0xF5;

    public static final int HTU21DF_WRITEREG = 0xE6;
    public static final int HTU21DF_READREG = 0xE7;
    public static final int HTU21DF_RESET = 0xFE;

    private I2C htu21df;

    public HTU21DF(Context pi4j) {
        this(pi4j, HTU21DF_ADDRESS);
    }

    public HTU21DF(Context pi4j, int address) {
        I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
        I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j).id("HTU21DF").bus(1).device(address).build();
        htu21df = i2CProvider.create(i2cConfig);
        LOG.info("HTU21DF Connected to bus {}. OK.", address);
    }

    public boolean begin() {
        reset();
        int r = 0;
        htu21df.write((byte) HTU21DF_READREG);
        r = htu21df.read();
        LOG.debug("DBG: Begin: 0x{}", lpad(Integer.toHexString(r), "0", 2));
        return (r == 0x02);
    }

    public void reset() {
        try {
            htu21df.write((byte) HTU21DF_RESET);
            LOG.debug("DBG: Reset OK");
        } finally {
            waitfor(15); // Wait 15ms
        }
    }

    @Override
    public void close() throws Exception {
        htu21df.close();
    }

    public float readTemperature() {
        // Reads the raw temperature from the sensor
        LOG.debug("Read Temp: Written 0x{}", lpad(Integer.toHexString((HTU21DF_READTEMP & 0xff)), "0", 2));
        htu21df.write((byte) (HTU21DF_READTEMP)); //  & 0xff));
        waitfor(50); // Wait 50ms
        byte[] buf = new byte[3];
        /*int rc  = */
        htu21df.read(buf, 0, 3);
        int msb = buf[0] & 0xFF;
        int lsb = buf[1] & 0xFF;
        int crc = buf[2] & 0xFF;
        int raw = ((msb << 8) + lsb) & 0xFFFC;

        LOG.debug("Temp -> 0x{} 0x{} 0x{}", lpad(Integer.toHexString(msb), "0", 2),
                lpad(Integer.toHexString(lsb), "0", 2), lpad(Integer.toHexString(crc), "0", 2));
        LOG.debug("DBG: Raw Temp: {} {}", (raw & 0xFFFF), raw);

        float temp = raw; // t;
        temp *= 175.72;
        temp /= 65536;
        temp -= 46.85;

        LOG.debug("DBG: Temp: {}", temp);
        return temp;
    }

    public float readHumidity() {
        // Reads the raw (uncompensated) humidity from the sensor
        htu21df.write((byte) HTU21DF_READHUM);
        waitfor(50); // Wait 50ms
        byte[] buf = new byte[3];
        /* int rc  = */
        htu21df.read(buf, 0, 3);
        int msb = buf[0] & 0xFF;
        int lsb = buf[1] & 0xFF;
        int crc = buf[2] & 0xFF;
        int raw = ((msb << 8) + lsb) & 0xFFFC;

        LOG.debug("Hum -> 0x{} 0x{} 0x{}", lpad(Integer.toHexString(msb), "0", 2),
                lpad(Integer.toHexString(lsb), "0", 2), lpad(Integer.toHexString(crc), "0", 2));
        LOG.debug("DBG: Raw Humidity: {} {}", (raw & 0xFFFF), raw);

        float hum = raw;
        hum *= 125;
        hum /= 65536;
        hum -= 6;

        LOG.debug("DBG: Humidity: {}", hum);
        return hum;
    }

    protected static void waitfor(long howMuch) {
        try {
            Thread.sleep(howMuch);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String lpad(String s, String with, int len) {
        String str = s;
        while (str.length() < len) {
            str = with + str;
        }
        return str;
    }

}
