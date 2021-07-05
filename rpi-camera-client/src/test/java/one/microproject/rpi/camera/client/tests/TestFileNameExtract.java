package one.microproject.rpi.camera.client.tests;

import one.microproject.rpi.camera.client.client.CameraClientImpl;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestFileNameExtract {

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                Arguments.of(null, null, "capture-image.jpg"),
                Arguments.of("", "", "capture-image.jpg"),
                Arguments.of("attachment; filename=capture-image.jpg", "image/jpeg", "capture-image.jpg"),
                Arguments.of("attachment; filename=capture-image.png", "image/png", "capture-image.png"),
                Arguments.of("", "image/jpeg", "capture-image.jpeg"),
                Arguments.of(null, "image/jpeg", "capture-image.jpeg"),
                Arguments.of("", "image/png", "capture-image.png"),
                Arguments.of(null, "image/png", "capture-image.png")
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void testGetFileNameFromHeader(String contentDisposition, String mimeType, String expectedResult) {
        String result = CameraClientImpl.getFileNameFromHeader(contentDisposition, mimeType);
        assertEquals(expectedResult, result);
    }

}
