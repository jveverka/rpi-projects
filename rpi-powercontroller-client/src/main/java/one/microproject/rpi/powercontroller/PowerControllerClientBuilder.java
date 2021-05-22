package one.microproject.rpi.powercontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import one.microproject.rpi.powercontroller.client.PowerControllerClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class PowerControllerClientBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(PowerControllerClientBuilder.class);

    private URL baseURL;
    private String clientId;
    private String clientSecret;
    private Long timeout = 3L;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private OkHttpClient client;
    private ObjectMapper mapper;

    public PowerControllerClientBuilder baseUrl(String baseURL) throws MalformedURLException {
        this.baseURL = new URL(baseURL);
        return this;
    }

    public PowerControllerClientBuilder withCredentials(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        return this;
    }

    public PowerControllerClientBuilder setTimeouts(Long timeout, TimeUnit timeUnit) {
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        return this;
    }

    public PowerControllerClientBuilder setHttpClient(OkHttpClient client) {
        this.client = client;
        return this;
    }

    public PowerControllerClientBuilder setObjectMapper(ObjectMapper mapper) {
        this.mapper = mapper;
        return this;
    }

    public PowerControllerClient build() {
        if (clientId == null || clientSecret == null) {
            throw new ClientException("Invalid client credentials !");
        }
        if (client == null) {
            LOG.info("default http client: timeouts={} {}", timeout, timeUnit);
            this.client = new OkHttpClient().newBuilder()
                    .connectTimeout(timeout, timeUnit)
                    .callTimeout(timeout, timeUnit)
                    .readTimeout(timeout, timeUnit)
                    .writeTimeout(timeout, timeUnit)
                    .build();
        }
        if (mapper == null) {
            LOG.info("default mapper");
            this.mapper = new ObjectMapper();
        }
        return new PowerControllerClientImpl(baseURL, clientId, clientSecret, client, mapper);
    }

    public PowerControllerReadClient buildReadClient() {
        return build();
    }

    public static PowerControllerClientBuilder builder() {
        return new PowerControllerClientBuilder();
    }

}
