package one.microproject.rpi.powercontroller.tests;

import one.microproject.rpi.powercontroller.services.AAService;
import one.microproject.rpi.powercontroller.services.impl.AAServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AAServiceTests {

    private static AAService aaService;

    @BeforeAll
    public static void init() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("client-001", "eiw9ohmaeraic5chah4aiz8aemooyoF9");
        aaService = new AAServiceImpl(credentials);
    }

    @Test
    @Order(1)
    public void testValidClientCredentials() {
        assertTrue(aaService.validateCredentials("client-001", "eiw9ohmaeraic5chah4aiz8aemooyoF9"));
    }

    @Test
    @Order(2)
    public void testInvalidClientCredentials() {
        assertFalse(aaService.validateCredentials("client-001", "xxw9ohmaeraic5chah4aiz8aemooyoF9"));
    }

    @Test
    @Order(3)
    public void testUnknownClientCredentials() {
        assertFalse(aaService.validateCredentials("client-002", "eiw9ohmaeraic5chah4aiz8aemooyoF9"));
    }

    @Test
    @Order(4)
    public void testUnknownClientInvalidCredentials() {
        assertFalse(aaService.validateCredentials("client-002", "xxw9ohmaeraic5chah4aiz8aemooyoF9"));
    }

}
