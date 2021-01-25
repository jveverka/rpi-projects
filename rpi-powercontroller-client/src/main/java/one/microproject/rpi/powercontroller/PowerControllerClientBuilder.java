package one.microproject.rpi.powercontroller;

import one.microproject.rpi.powercontroller.client.PowerControllerClientImpl;

public class PowerControllerClientBuilder {

    private String baseURL;
    private String userName;
    private String password;

    public PowerControllerClientBuilder baseUrl(String baseURL) {
        this.baseURL = baseURL;
        return this;
    }

    public PowerControllerClientBuilder withCredentials(String userName, String password) {
        this.userName = userName;
        this.password = password;
        return this;
    }

    public PowerControllerClient build() {
        return new PowerControllerClientImpl(baseURL, userName, password);
    }

    public static PowerControllerClientBuilder builder() {
        return new PowerControllerClientBuilder();
    }

}
