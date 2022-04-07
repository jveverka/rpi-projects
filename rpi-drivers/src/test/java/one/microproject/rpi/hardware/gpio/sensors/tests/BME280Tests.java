package one.microproject.rpi.hardware.gpio.sensors.tests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static one.microproject.rpi.hardware.gpio.sensors.impl.Utils.compensateTemperatureBME280;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BME280Tests {

    private static Stream<Arguments> provideTemperature() {
        return Stream.of(
                Arguments.of(55808, 38510, 2152, 12800, -19.8799991607666f)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTemperature")
    void testTemperatureCompensation(int rawTemp, int digT1, int digT2, int digT3, float expecterTemperature) {
        float temperature = compensateTemperatureBME280(rawTemp, digT1, digT2, digT3);
        assertEquals(expecterTemperature, temperature);
    }

}
