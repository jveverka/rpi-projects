package one.microproject.rpi.hardware.gpio.sensors.impl;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import one.microproject.rpi.hardware.gpio.sensors.BME280;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static one.microproject.rpi.hardware.gpio.sensors.impl.Utils.compensateTemperature;
import static one.microproject.rpi.hardware.gpio.sensors.impl.Utils.getRawValue;
import static one.microproject.rpi.hardware.gpio.sensors.impl.Utils.getSigned;
import static one.microproject.rpi.hardware.gpio.sensors.impl.Utils.waitfor;

public class BME280Impl implements BME280 {

    private static final Logger LOG = LoggerFactory.getLogger(BME280Impl.class);

    public static final int ADDRESS = 0x76;
    private static final int ID_REGISTER     = 0xD0;
    private static final int RESET_REGISTER  = 0xE0;
    private static final int STATUS_REGISTER = 0xF3;
    private static final int CTRL_MEAS = 0xF4;
    private static final int CTRL_HUM  = 0xF2;

    private int digT1;
    private int digT2;
    private int digT3;

    private final int address;
    private final String deviceId;
    private final Context context;
    private final int i2cBus;
    private final I2C bme280;

    public BME280Impl(Context pi4j) {
        this(pi4j, ADDRESS, 1);
    }

    public BME280Impl(Context pi4j, int address, int i2cBus) {
        this.address = address;
        this.deviceId = "BME280";
        this.context = pi4j;
        this.i2cBus = i2cBus;
        I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
        I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j).id(deviceId).bus(i2cBus).device(address).build();
        bme280 = i2CProvider.create(i2cConfig);
        LOG.info("BME280 Connected to i2c bus={} address={}. OK.", i2cBus, address);
        readCalibrationData();
    }

    private void readCalibrationData() {
        LOG.debug("readCalibrationData:");
        this.digT1 = readU16(0x88);
        this.digT2 = readS16(0x8A);
        this.digT3 = readS16(0x8C);
        LOG.debug("digT1={}", digT1);
        LOG.debug("digT2={}", digT2);
        LOG.debug("digT3={}", digT3);
    }

    private int readU8(int register) {
        // "Read an unsigned byte from the I2C device"
        int result = bme280.readRegister(register);
        LOG.debug("I2C: Device returned {} from reg {}", result, register);
        return result;
    }

    private int readS8(int register) {
        // "Reads a signed byte from the I2C device"
        int result = bme280.readRegister(register);
        result = getSigned(result);
        LOG.debug("I2C: Device returned {} from reg {}", result, register);
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

    private int readRawTemp() {
        // Reads the raw (uncompensated) temperature from the sensor
        bme280.write((byte)CTRL_HUM, (byte)0x01);
        bme280.write((byte)CTRL_MEAS, (byte)0b00100111);
        waitfor(5);
        int msb = readU8(0xFA);
        int lsb = readU8(0xFB);
        int xlsb = readU8(0xFC);
        int raw = getRawValue(msb, lsb, xlsb);
        LOG.info("DBG: Raw Temp: {}", raw);
        return raw;
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
    public int getId() {
        return bme280.readRegister(ID_REGISTER);
    }

    @Override
    public void reset() {
        bme280.writeRegister(RESET_REGISTER, 0xB6);
    }

    @Override
    public int getStatus() {
        return bme280.readRegister(STATUS_REGISTER);
    }

    @Override
    public float getTemperature() {
        int rawTemp = readRawTemp();
        return compensateTemperature(rawTemp, digT1, digT2, digT3);
    }

    @Override
    public float getPressure() {
        return 0;
    }

    @Override
    public float getHumidity() {
        return 0;
    }

    @Override
    public void close() throws Exception {
        bme280.close();
    }

}
