package one.microproject.rpi.powercontroller.tests;

import one.microproject.rpi.powercontroller.handlers.HandlerUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicAuthorizationTests {

    private static String basicAuthorization = "Basic Y2xpZW50LTAwMTplaXc5b2htYWVyYWljNWNoYWg0YWl6OGFlbW9veW9GOQ==";
    private static String clientId = "client-001";
    private static String clientSecret = "eiw9ohmaeraic5chah4aiz8aemooyoF9";

    @Test
    public void testAuthorizationHeaderDecoding()  {
        String[] credentials = HandlerUtils.extractCredentialsFromBasicAuthorization(basicAuthorization);
        assertEquals(clientId, credentials[0]);
        assertEquals(clientSecret, credentials[1]);
    }

    @Test
    public void testAuthorizationHeaderEncoding()  {
        String authString = HandlerUtils.createBasicAuthorizationFromCredentials(clientId, clientSecret);
        assertEquals(basicAuthorization, authString);
    }

}
