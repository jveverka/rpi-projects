package one.microproject.devicecontroller.config;

import okhttp3.OkHttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="app.system")
public class AppConfig {

    private String id;

    @Bean
    public OkHttpClient getOkHttpClient() {
        return new OkHttpClient().newBuilder().build();
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
