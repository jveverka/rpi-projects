package one.microproject.rpi.hardware.gpio.sensors.sensors;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * BMP-180 I2C
 * 3.3V Bosch temperature and barometric pressure sensor.
 *
 * @author gergej
 */
public class BMP180 implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(BMP180.class);

    public static final int LITTLE_ENDIAN = 0;
    public static final int BIG_ENDIAN = 1;
    private static final int BMP180_ENDIANNESS = BIG_ENDIAN;

    public static final int BMP180_ADDRESS = 0x77;
    // Operating Modes
    public static final int BMP180_ULTRALOWPOWER = 0;
    public static final int BMP180_STANDARD = 1;
    public static final int BMP180_HIGHRES = 2;
    public static final int BMP180_ULTRAHIGHRES = 3;

    // BMP180 Registers
    public static final int BMP180_CAL_AC1 = 0xAA;  // R   Calibration data (16 bits)
    public static final int BMP180_CAL_AC2 = 0xAC;  // R   Calibration data (16 bits)
    public static final int BMP180_CAL_AC3 = 0xAE;  // R   Calibration data (16 bits)
    public static final int BMP180_CAL_AC4 = 0xB0;  // R   Calibration data (16 bits)
    public static final int BMP180_CAL_AC5 = 0xB2;  // R   Calibration data (16 bits)
    public static final int BMP180_CAL_AC6 = 0xB4;  // R   Calibration data (16 bits)
    public static final int BMP180_CAL_B1 = 0xB6;  // R   Calibration data (16 bits)
    public static final int BMP180_CAL_B2 = 0xB8;  // R   Calibration data (16 bits)
    public static final int BMP180_CAL_MB = 0xBA;  // R   Calibration data (16 bits)
    public static final int BMP180_CAL_MC = 0xBC;  // R   Calibration data (16 bits)
    public static final int BMP180_CAL_MD = 0xBE;  // R   Calibration data (16 bits)

    public static final int BMP180_CONTROL = 0xF4;
    public static final int BMP180_TEMPDATA = 0xF6;
    public static final int BMP180_PRESSUREDATA = 0xF6;
    public static final int BMP180_READTEMPCMD = 0x2E;
    public static final int BMP180_READPRESSURECMD = 0x34;

    private int calAC1 = 0;
    private int calAC2 = 0;
    private int calAC3 = 0;
    private int calAC4 = 0;
    private int calAC5 = 0;
    private int calAC6 = 0;
    private int calB1 = 0;
    private int calB2 = 0;
    private int calMB = 0;
    private int calMC = 0;
    private int calMD = 0;

    private final I2C bmp180;
    private int mode = BMP180_STANDARD;

    public BMP180(Context pi4j) {
        this(pi4j, BMP180_ADDRESS);
    }

    public BMP180(Context pi4j, int address) {
		I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
		I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j).id("BMP180").bus(1).device(address).build();
		bmp180 = i2CProvider.create(i2cConfig);
        LOG.info("Connected to bus. OK.");
        readCalibrationData();
    }

    private int readU8(int reg) {
        // "Read an unsigned byte from the I2C device"
        int result = 0;
        try {
            result = this.bmp180.readRegister(reg);
            LOG.info("I2C: Device {} returned {} from reg {}", BMP180_ADDRESS, result, reg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private int readS8(int reg) {
        // "Reads a signed byte from the I2C device"
        int result = 0;
        try {
            result = this.bmp180.readRegister(reg);
            if (result > 127) {
                result -= 256;
            }
            LOG.info("I2C: Device {} returned {} from reg {}", BMP180_ADDRESS, result, reg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private int readU16(int register) {
        int hi = this.readU8(register);
        int lo = this.readU8(register + 1);
        return (BMP180_ENDIANNESS == BIG_ENDIAN) ? (hi << 8) + lo : (lo << 8) + hi; // Big Endian
    }

    private int readS16(int register) {
        int hi = 0, lo = 0;
        if (BMP180_ENDIANNESS == BIG_ENDIAN) {
            hi = this.readS8(register);
            lo = this.readU8(register + 1);
        } else {
            lo = this.readS8(register);
            hi = this.readU8(register + 1);
        }
        return (hi << 8) + lo;
    }

    public void readCalibrationData() {
        // Reads the calibration data from the IC
        calAC1 = readS16(BMP180_CAL_AC1);   // INT16
        calAC2 = readS16(BMP180_CAL_AC2);   // INT16
        calAC3 = readS16(BMP180_CAL_AC3);   // INT16
        calAC4 = readU16(BMP180_CAL_AC4);   // UINT16
        calAC5 = readU16(BMP180_CAL_AC5);   // UINT16
        calAC6 = readU16(BMP180_CAL_AC6);   // UINT16
        calB1 = readS16(BMP180_CAL_B1);    // INT16
        calB2 = readS16(BMP180_CAL_B2);    // INT16
        calMB = readS16(BMP180_CAL_MB);    // INT16
        calMC = readS16(BMP180_CAL_MC);    // INT16
        calMD = readS16(BMP180_CAL_MD);    // INT16
        showCalibrationData();
    }

    private void showCalibrationData() {
        // Displays the calibration values for debugging purposes
        LOG.info("DBG: AC1 = {}", calAC1);
        LOG.info("DBG: AC2 = {}", calAC2);
        LOG.info("DBG: AC3 = {}", calAC3);
        LOG.info("DBG: AC4 = {}", calAC4);
        LOG.info("DBG: AC5 = {}", calAC5);
        LOG.info("DBG: AC6 = {}", calAC6);
        LOG.info("DBG: B1  = {}", calB1);
        LOG.info("DBG: B2  = {}", calB2);
        LOG.info("DBG: MB  = {}", calMB);
        LOG.info("DBG: MC  = {}", calMC);
        LOG.info("DBG: MD  = {}", calMD);
    }

    public int readRawTemp() {
        // Reads the raw (uncompensated) temperature from the sensor
        bmp180.write((byte)BMP180_CONTROL, (byte) BMP180_READTEMPCMD);
        waitfor(5);  // Wait 5ms
        int raw = readU16(BMP180_TEMPDATA);
        LOG.info("DBG: Raw Temp: {}, {}", (raw & 0xFFFF), raw);
        return raw;
    }

    public int readRawPressure() {
        // Reads the raw (uncompensated) pressure level from the sensor
        bmp180.write((byte)BMP180_CONTROL, (byte) (BMP180_READPRESSURECMD + (this.mode << 6)));
        if (this.mode == BMP180_ULTRALOWPOWER) {
            waitfor(5);
        } else if (this.mode == BMP180_HIGHRES) {
            waitfor(14);
        } else if (this.mode == BMP180_ULTRAHIGHRES) {
            waitfor(26);
        } else {
            waitfor(8);
        }
        int msb = bmp180.readRegister(BMP180_PRESSUREDATA);
        int lsb = bmp180.readRegister(BMP180_PRESSUREDATA + 1);
        int xlsb = bmp180.readRegister(BMP180_PRESSUREDATA + 2);
        int raw = ((msb << 16) + (lsb << 8) + xlsb) >> (8 - this.mode);
        LOG.info("DBG: Raw Pressure: {}, {}", (raw & 0xFFFF), raw);
        return raw;
    }

    public float readTemperature() {
        // Gets the compensated temperature in degrees celcius
        int UT = 0;
        int X1 = 0;
        int X2 = 0;
        int B5 = 0;
        float temp = 0.0f;

        // Read raw temp before aligning it with the calibration values
        UT = this.readRawTemp();
        X1 = ((UT - this.calAC6) * this.calAC5) >> 15;
        X2 = (this.calMC << 11) / (X1 + this.calMD);
        B5 = X1 + X2;
        temp = ((B5 + 8) >> 4) / 10.0f;
        LOG.info("DBG: Calibrated temperature = {} C", temp);
        return temp;
    }

    public float readPressure() {
        // Gets the compensated pressure in pascal
        int UT = 0;
        int UP = 0;
        int B3 = 0;
        int B5 = 0;
        int B6 = 0;
        int X1 = 0;
        int X2 = 0;
        int X3 = 0;
        int p = 0;
        int B4 = 0;
        int B7 = 0;

        UT = this.readRawTemp();
        UP = this.readRawPressure();

        // You can use the datasheet values to test the conversion results
        // boolean dsValues = true;
        boolean dsValues = false;

        if (dsValues) {
            UT = 27898;
            UP = 23843;
            this.calAC6 = 23153;
            this.calAC5 = 32757;
            this.calMB = -32768;
            this.calMC = -8711;
            this.calMD = 2868;
            this.calB1 = 6190;
            this.calB2 = 4;
            this.calAC3 = -14383;
            this.calAC2 = -72;
            this.calAC1 = 408;
            this.calAC4 = 32741;
            this.mode = BMP180_ULTRALOWPOWER;
            this.showCalibrationData();
        }
        // True Temperature Calculations
        X1 = (int) ((UT - this.calAC6) * this.calAC5) >> 15;
        X2 = (this.calMC << 11) / (X1 + this.calMD);
        B5 = X1 + X2;
        LOG.debug("DBG: X1 = {}", X1);
        LOG.debug("DBG: X2 = {}", X2);
        LOG.debug("DBG: B5 = {}", B5);
        LOG.debug("DBG: True Temperature = {} C", (((B5 + 8) >> 4) / 10.0));
        // Pressure Calculations
        B6 = B5 - 4000;
        X1 = (this.calB2 * (B6 * B6) >> 12) >> 11;
        X2 = (this.calAC2 * B6) >> 11;
        X3 = X1 + X2;
        B3 = (((this.calAC1 * 4 + X3) << this.mode) + 2) / 4;
        LOG.debug("DBG: B6 = {}", B6);
        LOG.debug("DBG: X1 = {}", X1);
        LOG.debug("DBG: X2 = {}", X2);
        LOG.debug("DBG: X3 = {}", X3);
        LOG.debug("DBG: B3 = {}", B3);
        X1 = (this.calAC3 * B6) >> 13;
        X2 = (this.calB1 * ((B6 * B6) >> 12)) >> 16;
        X3 = ((X1 + X2) + 2) >> 2;
        B4 = (this.calAC4 * (X3 + 32768)) >> 15;
        B7 = (UP - B3) * (50000 >> this.mode);
        LOG.debug("DBG: X1 = {}", X1);
        LOG.debug("DBG: X2 = {}", X2);
        LOG.debug("DBG: X3 = {}", X3);
        LOG.debug("DBG: B4 = {}", B4);
        LOG.debug("DBG: B7 = {}", B7);
        if (B7 < 0x80000000) {
            p = (B7 * 2) / B4;
        } else {
            p = (B7 / B4) * 2;
        }
        LOG.info("DBG: X1 = {}", X1);

        X1 = (p >> 8) * (p >> 8);
        X1 = (X1 * 3038) >> 16;
        X2 = (-7357 * p) >> 16;
        LOG.debug("DBG: p  = {}", p);
        LOG.debug("DBG: X1 = {}", X1);
        LOG.debug("DBG: X2 = {}", X2);
        p = p + ((X1 + X2 + 3791) >> 4);
        LOG.info("DBG: Pressure = {} Pa", p);
        return p;
    }

    protected static void waitfor(long howMuch) {
        try {
            Thread.sleep(howMuch);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        bmp180.close();
    }

}
