package one.microproject.rpi.powercontroller.handlers;

import io.undertow.server.HttpServerExchange;
import one.microproject.rpi.powercontroller.services.AAService;

import java.util.Base64;

public final class HandlerUtils {

    private HandlerUtils() {
    }

    public static final String JSON_TYPE = "application/json";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_PUT = "PUT";

    public static final int NOT_FOUND = 404;
    public static final int FORBIDDEN = 403;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int OK = 200;

    public static boolean validateRequest(AAService aaService, HttpServerExchange exchange) {
        try {
            String authorization = exchange.getRequestHeaders().get("Authorization").getFirst();
            if (authorization != null && authorization.startsWith("Basic ")) {
                String[] split = extractCredentialsFromBasicAuthorization(authorization);
                return aaService.validateCredentials(split[0], split[1]);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static String[] extractCredentialsFromBasicAuthorization(String basicAuthorization) {
        String[] encoded = basicAuthorization.split(" ");
        byte[] decodedBytes = Base64.getDecoder().decode(encoded[1].trim());
        String decodedString = new String(decodedBytes);
        return decodedString.split(":");
    }

    public static String createBasicAuthorizationFromCredentials(String clientId, String clientSecret) {
        String authorization = clientId + ":" + clientSecret;
        byte[] encodedBytes = Base64.getEncoder().encode(authorization.getBytes());
        String encodedString = new String(encodedBytes);
        return "Basic " + encodedString;
    }

}
