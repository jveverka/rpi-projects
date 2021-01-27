package one.microproject.rpi.powercontroller;

import one.microproject.rpi.powercontroller.client.PowerControllerClientImpl;

public class PowerControllerClientBuilder {

    private String baseURL;
    private String clientId;
    private String clientSecret;

    public PowerControllerClientBuilder baseUrl(String baseURL) {
        this.baseURL = baseURL;
        return this;
    }

    public PowerControllerClientBuilder withCredentials(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        return this;
    }

    public PowerControllerClient build() {
        return new PowerControllerClientImpl(baseURL, clientId, clientSecret);
    }

    public PowerControllerReadClient buildReadClient() {
        return new PowerControllerClientImpl(baseURL, clientId, clientSecret);
    }

    public static PowerControllerClientBuilder builder() {
        return new PowerControllerClientBuilder();
    }

}
