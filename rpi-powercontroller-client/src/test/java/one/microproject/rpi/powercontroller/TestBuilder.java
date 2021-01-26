package one.microproject.rpi.powercontroller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TestBuilder {

    @Test
    void testBuilder() {
        PowerControllerClient powerControllerClient = PowerControllerClientBuilder.builder()
                .baseUrl("http://localhost:8090")
                .withCredentials("client-001", "secret")
                .build();
        assertNotNull(powerControllerClient);
    }

}
