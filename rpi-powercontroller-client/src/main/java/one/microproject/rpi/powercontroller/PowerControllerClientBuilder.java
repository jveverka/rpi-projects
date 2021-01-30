package one.microproject.rpi.powercontroller;

import one.microproject.rpi.powercontroller.client.PowerControllerClientImpl;

import java.net.MalformedURLException;
import java.net.URL;

public class PowerControllerClientBuilder {

    private URL baseURL;
    private String clientId;
    private String clientSecret;

    public PowerControllerClientBuilder baseUrl(String baseURL) throws MalformedURLException {
        this.baseURL = new URL(baseURL);
        return this;
    }

    public PowerControllerClientBuilder withCredentials(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        return this;
    }

    public PowerControllerClient build() {
        if (clientId == null || clientSecret == null) {
            throw new ClientException("Invalid client credentials !");
        }
        return new PowerControllerClientImpl(baseURL, clientId, clientSecret);
    }

    public PowerControllerReadClient buildReadClient() {
        return build();
    }

    public static PowerControllerClientBuilder builder() {
        return new PowerControllerClientBuilder();
    }

}
