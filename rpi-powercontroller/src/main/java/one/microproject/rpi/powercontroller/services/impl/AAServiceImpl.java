package one.microproject.rpi.powercontroller.services.impl;

import one.microproject.rpi.powercontroller.services.AAService;

import java.util.Map;

public class AAServiceImpl implements AAService {

    private final Map<String, String> credentials;

    public AAServiceImpl(Map<String, String> credentials) {
        this.credentials = credentials;
    }

    @Override
    public boolean validateCredentials(String clientId, String clientSecret) {
        String secret = credentials.get(clientId);
        if (secret != null) {
            return secret.equals(clientSecret);
        } else {
            return false;
        }
    }

}
