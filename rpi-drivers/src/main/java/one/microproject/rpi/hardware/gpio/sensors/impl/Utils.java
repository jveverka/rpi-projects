package one.microproject.rpi.hardware.gpio.sensors.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    private Utils() {
    }

    public static float compensateTemperature(int rawTemp, int digT1, int digT2, int digT3) {
        LOG.info("compensateTemperature: rawTemp={}, digT1={}, digT2={}, digT3={}", rawTemp, digT1, digT2, digT3);

        int var1 = ((((rawTemp>>3) - (digT1<<1))) * (digT2)) >> 11;
        int var2 = (((((rawTemp>>4) - (digT1)) * ((rawTemp>>4) - (digT1))) >> 12) * (digT3)) >> 14;

        int tFine = var1 + var2;
        int t = (tFine * 5 + 128) >> 8;
        LOG.info("compensateTemperature: var1={}, var2={}, T={}", var1, var2, t);
        float temp = t / 100.0f;
        LOG.info("compensateTemperature: Calibrated temperature = {} C", temp);
        return temp;
    }

    public static int getRawValue(int msb, int lsb, int xlsb) {
        LOG.info("getRawValue: msb={} lsb={} xlsb={}", msb, lsb, xlsb);
        return (((msb << 8) + lsb)<<4) + (xlsb>>4);
    }

    public static int getRawValue(int msb, int lsb) {
        LOG.info("getRawValue: msb={} lsb={}", msb, lsb);
        return (msb << 8) + lsb;
    }

    public static int getSigned(int unsigned) {
        if (unsigned > 127) {
            unsigned -= 256;
        }
        return unsigned;
    }

    protected static void waitfor(long howMuch) {
        try {
            Thread.sleep(howMuch);
        } catch (InterruptedException e) {
            LOG.error("Error: ", e);
        }
    }

}
