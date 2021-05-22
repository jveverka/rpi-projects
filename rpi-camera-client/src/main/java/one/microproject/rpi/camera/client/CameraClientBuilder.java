package one.microproject.rpi.camera.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import one.microproject.rpi.camera.client.client.CameraClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class CameraClientBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(CameraClientBuilder.class);

    private URL baseURL;
    private String clientId;
    private String clientSecret;
    private Long timeout = 3L;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private OkHttpClient client;
    private ObjectMapper mapper;

    public CameraClientBuilder baseUrl(String baseURL) throws MalformedURLException {
        this.baseURL = new URL(baseURL);
        return this;
    }

    public CameraClientBuilder withCredentials(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        return this;
    }

    public CameraClientBuilder setTimeouts(Long timeout, TimeUnit timeUnit) {
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        return this;
    }

    public CameraClientBuilder setHttpClient(OkHttpClient client) {
        this.client = client;
        return this;
    }

    public CameraClientBuilder setObjectMapper(ObjectMapper mapper) {
        this.mapper = mapper;
        return this;
    }

    public CameraClientBuilder setTimeout(Long timeout, TimeUnit timeUnit) {
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        return this;
    }

    public CameraClient build() {
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
        return new CameraClientImpl(baseURL, clientId, clientSecret, client, mapper);
    }

    public static CameraClientBuilder builder() {
        return new CameraClientBuilder();
    }

}
