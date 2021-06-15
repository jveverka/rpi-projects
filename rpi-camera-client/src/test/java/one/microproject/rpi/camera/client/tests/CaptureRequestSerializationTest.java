package one.microproject.rpi.camera.client.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.rpi.camera.client.dto.CameraConfiguration;
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
        CameraConfiguration captureRequest = new CameraConfiguration(0.1F, ImageFormat.JPEG, Resolution.M5, Rotation.D180, 85);
        String data = mapper.writeValueAsString(captureRequest);
        CameraConfiguration deserialized = mapper.readValue(data, CameraConfiguration.class);
        assertEquals(captureRequest.getShutterSpeed(), deserialized.getShutterSpeed());
        assertEquals(captureRequest.getImageFormat(), deserialized.getImageFormat());
        assertEquals(captureRequest.getRotation(), deserialized.getRotation());
        assertEquals(captureRequest.getResolution(), deserialized.getResolution());
        assertEquals(captureRequest.getQuality(), deserialized.getQuality());
    }

}
