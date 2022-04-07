package one.microproject.rpi.hardware.gpio.sensors.tests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static one.microproject.rpi.hardware.gpio.sensors.impl.Utils.compensateTemperatureBME280;
import static one.microproject.rpi.hardware.gpio.sensors.impl.Utils.compensateTemperatureBMP180;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTests {

    private static Stream<Arguments> provideTemperatureBME280() {
        return Stream.of(
                Arguments.of(55808, 38510, 2152, 12800, -19.8799991607666f)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTemperatureBME280")
    void testTemperatureCompensationBME280(int rawTemp, int digT1, int digT2, int digT3, float expecterTemperature) {
        float temperature = compensateTemperatureBME280(rawTemp, digT1, digT2, digT3);
        assertEquals(expecterTemperature, temperature);
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
    void testTemperatureCompensationBMP180(int ut, int calAC6, int calAC5, int calMC, int calMD, float expecterTemperature) {
        float temperature = compensateTemperatureBMP180(ut, calAC6, calAC5, calMC, calMD);
        assertEquals(expecterTemperature, temperature);
    }

}
