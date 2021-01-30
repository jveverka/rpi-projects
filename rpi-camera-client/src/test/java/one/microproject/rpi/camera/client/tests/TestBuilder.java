package one.microproject.rpi.camera.client.tests;

import one.microproject.rpi.camera.client.CameraClient;
import one.microproject.rpi.camera.client.CameraClientBuilder;
import one.microproject.rpi.camera.client.ClientException;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestBuilder {

    @Test
    void testClientBuilder() throws MalformedURLException {
        CameraClient cameraClient = CameraClientBuilder.builder()
                .baseUrl("http://localhost:8090")
                .withCredentials("client-001", "secret")
                .build();
        assertNotNull(cameraClient);
    }

    @Test
    void testInvalidUrlClientBuilder() {
        assertThrows(MalformedURLException.class, () -> {
            CameraClientBuilder.builder()
                    .baseUrl("invalid-url")
                    .withCredentials("client-001", "secret")
                    .build();
        });
    }

    @Test
    void testInvalidCredentialsClientBuilder() {
        assertThrows(ClientException.class, () -> {
            CameraClientBuilder.builder()
                    .baseUrl("http://localhost:8090")
                    .build();
        });
    }

}