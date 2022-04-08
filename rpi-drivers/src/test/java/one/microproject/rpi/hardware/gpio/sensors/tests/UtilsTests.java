package one.microproject.rpi.hardware.gpio.sensors.tests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static one.microproject.rpi.hardware.gpio.sensors.impl.Utils.compensateDataBME280;
import static one.microproject.rpi.hardware.gpio.sensors.impl.Utils.compensateTemperatureBMP180;
import static one.microproject.rpi.hardware.gpio.sensors.impl.Utils.getRawValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTests {

    private static Stream<Arguments> provideTemperatureBME280() {
        byte[] data1 = { 88, -56, 0, -127, -94, 0, 94, 66 };
        byte[] data2 = { 89, 56, 0, -125, 57, 0, 127, -63 };
        byte[] data3 = { 90, 61, 0, -122, -49, 0, 123, -125 };
        return Stream.of(
                Arguments.of(data1, 28310, 26632, 50, 36247, -10647, 3024, 8029, -7, -7, 9900, -10230, 4285, 75, 379, 0, 280, 50, 30, 24.77f, 35.632812f, 95809.705f),
                Arguments.of(data2, 28310, 26632, 50, 36247, -10647, 3024, 8029, -7, -7, 9900, -10230, 4285, 75, 379, 0, 280, 50, 30, 26.84f, 85.643555f, 95815.56f),
                Arguments.of(data3, 28310, 26632, 50, 36247, -10647, 3024, 8029, -7, -7, 9900, -10230, 4285, 75, 379, 0, 280, 50, 30, 31.5f, 79.740234f, 95799.26f)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTemperatureBME280")
    void testTemperatureCompensationBME280(byte[] data, int digT1, int digT2, int digT3,
                                           int digP1, int digP2, int digP3, int digP4, int digP5, int digP6, int digP7, int digP8, int digP9,
                                           int digH1, int digH2, int digH3, int digH4, int digH5, int digH6,
                                           float expectedTemperature, float expectedHumidity, float expectedPressure) {
        float[] floats = compensateDataBME280(data, digT1, digT2, digT3,
                digP1, digP2, digP3, digP4, digP5, digP6, digP7, digP8, digP9,
                digH1, digH2, digH3, digH4, digH5, digH6);
        assertEquals(expectedTemperature, floats[0]);
        assertEquals(expectedHumidity, floats[1]);
        assertEquals(expectedPressure, floats[2]);
    }

    private static Stream<Arguments> provideTemperatureBMP180() {
        return Stream.of(
                Arguments.of(27989, 19017, 24954, -11786, 2848, 27.1f),
                Arguments.of(28212, 19017, 24954, -11786, 2848, 28.5f),
                Arguments.of(28299, 19017, 24954, -11786, 2848, 29.0f),
                Arguments.of(28410, 19017, 24954, -11786, 2848, 29.6f),
                Arguments.of(28031, 19017, 24954, -11786, 2848, 27.4f)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTemperatureBMP180")
    void testTemperatureCompensationBMP180(int ut, int calAC6, int calAC5, int calMC, int calMD, float expectedTemperature) {
        float temperature = compensateTemperatureBMP180(ut, calAC6, calAC5, calMC, calMD);
        assertEquals(expectedTemperature, temperature);
    }

    private static Stream<Arguments> provideGetRawValue20Bit() {
        return Stream.of(
                Arguments.of(129, 227, 0, 532016),
                Arguments.of(130, 32, 0, 532992),
                Arguments.of(130, 31, 0, 532976),
                Arguments.of(130, 31, 16, 532977),
                Arguments.of(130, 33, 0, 533008)
        );
    }

    @ParameterizedTest
    @MethodSource("provideGetRawValue20Bit")
    void testGetRawValue20Bit(int msb, int lsb, int xlsb, int expectedRaw) {
        int raw = getRawValue(msb, lsb, xlsb);
        assertEquals(expectedRaw, raw);
    }

    private static Stream<Arguments> provideGetRawValue16Bit() {
        return Stream.of(
                Arguments.of(129, 227, 33251)
        );
    }

    @ParameterizedTest
    @MethodSource("provideGetRawValue16Bit")
    void testGetRawValue16Bit(int msb, int lsb, int expectedRaw) {
        int raw = getRawValue(msb, lsb);
        assertEquals(expectedRaw, raw);
    }

}
