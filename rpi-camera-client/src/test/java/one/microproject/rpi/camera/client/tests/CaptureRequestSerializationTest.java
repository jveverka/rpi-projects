package one.microproject.rpi.camera.client.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.rpi.camera.client.dto.CameraConfiguration;
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
        CameraConfiguration captureRequest = new CameraConfiguration("R1", "D0",  24);
        String data = mapper.writeValueAsString(captureRequest);
        CameraConfiguration deserialized = mapper.readValue(data, CameraConfiguration.class);
        assertEquals(captureRequest.getRotation(), deserialized.getRotation());
        assertEquals(captureRequest.getResolution(), deserialized.getResolution());
        assertEquals(captureRequest.getFramerate(), deserialized.getFramerate());
    }

}
