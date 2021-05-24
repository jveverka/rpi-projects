package one.microproject.devicecontroller.config;

import okhttp3.OkHttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public OkHttpClient getOkHttpClient() {
        return new OkHttpClient().newBuilder().build();
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

}
