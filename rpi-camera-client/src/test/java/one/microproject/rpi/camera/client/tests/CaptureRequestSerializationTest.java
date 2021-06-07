package one.microproject.rpi.camera.client.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.rpi.camera.client.dto.CaptureRequest;
import one.microproject.rpi.camera.client.dto.ImageFormat;
import one.microproject.rpi.camera.client.dto.Resolution;
import one.microproject.rpi.camera.client.dto.Rotation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CaptureRequestSerializationTest {

    private static ObjectMapper mapper;

    @BeforeAll
    public static void init() {
        mapper = new ObjectMapper();
    }

    @Test
    void testSerializationAndDeserialization() throws JsonProcessingException {
        CaptureRequest captureRequest = new CaptureRequest(0.1F, ImageFormat.JPEG, Resolution.M5, Rotation.D180);
        String data = mapper.writeValueAsString(captureRequest);
        CaptureRequest deserialized = mapper.readValue(data, CaptureRequest.class);
        assertEquals(captureRequest.getShutterSpeed(), deserialized.getShutterSpeed());
        assertEquals(captureRequest.getImageFormat(), deserialized.getImageFormat());
        assertEquals(captureRequest.getRotation(), deserialized.getRotation());
        assertEquals(captureRequest.getResolution(), deserialized.getResolution());
    }

}
