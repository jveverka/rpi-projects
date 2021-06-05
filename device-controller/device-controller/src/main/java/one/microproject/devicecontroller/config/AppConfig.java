package one.microproject.devicecontroller.config;

import okhttp3.OkHttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Configuration
@ConfigurationProperties(prefix="app.system")
public class AppConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AppConfig.class);

    private String id;

    @PostConstruct
    public void init() {
        String timeZone = TimeZone.getDefault().getID();
        LOG.info("## timezone {}", timeZone);
    }

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
