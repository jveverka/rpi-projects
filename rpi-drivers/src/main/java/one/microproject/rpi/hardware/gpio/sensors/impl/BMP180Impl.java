package one.microproject.rpi.hardware.gpio.sensors.impl;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import one.microproject.rpi.hardware.gpio.sensors.BMP180;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static one.microproject.rpi.hardware.gpio.sensors.impl.Utils.getRawValue;
import static one.microproject.rpi.hardware.gpio.sensors.impl.Utils.getSigned;
import static one.microproject.rpi.hardware.gpio.sensors.impl.Utils.waitfor;


/**
 * BMP-180 I2C
 * 3.3V Bosch temperature and barometric pressure sensor.
 *
 * @author gergej
 */
public class BMP180Impl implements BMP180 {

    private static final Logger LOG = LoggerFactory.getLogger(BMP180Impl.class);

    public static final int ADDRESS = 0x77;

    private static final int ID_REGISTER    = 0xD0;
    private static final int RESET_REGISTER = 0xE0;

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
    private int mode = BMP180_STANDARD;

    private final int address;
    private final String deviceId;
    private final Context context;
    private final int i2cBus;
    private final I2C bmp180;

    public BMP180Impl(Context pi4j) {
        this(pi4j, ADDRESS, 1);
    }

    public BMP180Impl(Context pi4j, int address, int i2cBus) {
        this.address = address;
        this.deviceId = "BMP180";
        this.context = pi4j;
        this.i2cBus = i2cBus;
		I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
		I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j).id(deviceId).bus(i2cBus).device(address).build();
		bmp180 = i2CProvider.create(i2cConfig);
        LOG.info("BMP180 Connected to i2c bus={} address={}. OK.", i2cBus, address);
        readCalibrationData();
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

    private int readU8(int reg) {
        // "Read an unsigned byte from the I2C device"
        int result = bmp180.readRegister(reg);
        LOG.debug("I2C: Device returned {} from reg {}", result, reg);
        return result;
    }

    private int readS8(int reg) {
        // "Reads a signed byte from the I2C device"
        int result = bmp180.readRegister(reg);
        result = getSigned(result);
        LOG.debug("I2C: Device returned {} from reg {}", result, reg);
        return result;
    }

    private int readU16(int register) {
        int msb = this.readU8(register);
        int lsb = this.readU8(register + 1);
        return getRawValue(msb, lsb);
    }

    private int readS16(int register) {
        int msb = this.readS8(register);
        int lsb = this.readU8(register + 1);
        return getRawValue(msb, lsb);
    }

    private void readCalibrationData() {
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
        LOG.debug("DBG: AC1 = {}", calAC1);
        LOG.debug("DBG: AC2 = {}", calAC2);
        LOG.debug("DBG: AC3 = {}", calAC3);
        LOG.debug("DBG: AC4 = {}", calAC4);
        LOG.debug("DBG: AC5 = {}", calAC5);
        LOG.debug("DBG: AC6 = {}", calAC6);
        LOG.debug("DBG: B1  = {}", calB1);
        LOG.debug("DBG: B2  = {}", calB2);
        LOG.debug("DBG: MB  = {}", calMB);
        LOG.debug("DBG: MC  = {}", calMC);
        LOG.debug("DBG: MD  = {}", calMD);
    }

    private int readRawTemp() {
        // Reads the raw (uncompensated) temperature from the sensor
        bmp180.write((byte)BMP180_CONTROL, (byte) BMP180_READTEMPCMD);
        waitfor(5);  // Wait 5ms
        int raw = readU16(BMP180_TEMPDATA);
        LOG.debug("DBG: Raw Temp: {}, {}", (raw & 0xFFFF), raw);
        return raw;
    }

    private int readRawPressure() {
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
        LOG.debug("DBG: Raw Pressure: {}, {}", (raw & 0xFFFF), raw);
        return raw;
    }

    @Override
    public int getId() {
        return bmp180.readRegister(ID_REGISTER);
    }

    @Override
    public void reset() {
        bmp180.writeRegister(RESET_REGISTER, 0xB6);
    }

    @Override
    public float getTemperature() {
        // Gets the compensated temperature in degrees celsius
        // Read raw temp before aligning it with the calibration values
        int UT = this.readRawTemp();
        int X1 = ((UT - this.calAC6) * this.calAC5) >> 15;
        int X2 = (this.calMC << 11) / (X1 + this.calMD);
        int B5 = X1 + X2;
        float temp = ((B5 + 8) >> 4) / 10.0f;
        LOG.debug("DBG: Calibrated temperature = {} C", temp);
        return temp;
    }

    @Override
    public float getPressure() {
        // Gets the compensated pressure in pascal
        int p = 0;
        int UT = this.readRawTemp();
        int UP = this.readRawPressure();

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
        int X1 = (int) ((UT - this.calAC6) * this.calAC5) >> 15;
        int X2 = (this.calMC << 11) / (X1 + this.calMD);
        int B5 = X1 + X2;
        LOG.debug("DBG: X1 = {}", X1);
        LOG.debug("DBG: X2 = {}", X2);
        LOG.debug("DBG: B5 = {}", B5);
        LOG.debug("DBG: True Temperature = {} C", (((B5 + 8) >> 4) / 10.0));
        // Pressure Calculations
        int B6 = B5 - 4000;
        X1 = (this.calB2 * (B6 * B6) >> 12) >> 11;
        X2 = (this.calAC2 * B6) >> 11;
        int X3 = X1 + X2;
        int B3 = (((this.calAC1 * 4 + X3) << this.mode) + 2) / 4;
        LOG.debug("DBG: B6 = {}", B6);
        LOG.debug("DBG: X1 = {}", X1);
        LOG.debug("DBG: X2 = {}", X2);
        LOG.debug("DBG: X3 = {}", X3);
        LOG.debug("DBG: B3 = {}", B3);
        X1 = (this.calAC3 * B6) >> 13;
        X2 = (this.calB1 * ((B6 * B6) >> 12)) >> 16;
        X3 = ((X1 + X2) + 2) >> 2;
        int B4 = (this.calAC4 * (X3 + 32768)) >> 15;
        int B7 = (UP - B3) * (50000 >> this.mode);
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
        LOG.debug("DBG: X1 = {}", X1);
        X1 = (p >> 8) * (p >> 8);
        X1 = (X1 * 3038) >> 16;
        X2 = (-7357 * p) >> 16;
        LOG.debug("DBG: p  = {}", p);
        LOG.debug("DBG: X1 = {}", X1);
        LOG.debug("DBG: X2 = {}", X2);
        p = p + ((X1 + X2 + 3791) >> 4);
        LOG.debug("DBG: Pressure = {} Pa", p);
        return p;
    }

    @Override
    public void close() throws Exception {
        bmp180.close();
    }

}
