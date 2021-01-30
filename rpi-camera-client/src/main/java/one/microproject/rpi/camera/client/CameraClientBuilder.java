package one.microproject.rpi.camera.client;

import one.microproject.rpi.camera.client.client.CameraClientImpl;

import java.net.MalformedURLException;
import java.net.URL;

public class CameraClientBuilder {

    private URL baseURL;
    private String clientId;
    private String clientSecret;

    public CameraClientBuilder baseUrl(String baseURL) throws MalformedURLException {
        this.baseURL = new URL(baseURL);
        return this;
    }

    public CameraClientBuilder withCredentials(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        return this;
    }

    public CameraClient build() {
        if (clientId == null || clientSecret == null) {
            throw new ClientException("Invalid client credentials !");
        }
        return new CameraClientImpl(baseURL, clientId, clientSecret);
    }

    public static CameraClientBuilder builder() {
        return new CameraClientBuilder();
    }

}
