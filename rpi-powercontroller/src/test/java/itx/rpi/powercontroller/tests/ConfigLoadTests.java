package itx.rpi.powercontroller.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import itx.rpi.powercontroller.config.Configuration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigLoadTests {

    @Test
    public void configLoadTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = ConfigLoadTests.class.getResourceAsStream("/configuration.json");
        Configuration configuration = mapper.readValue(is, Configuration.class);
        assertNotNull(configuration);
    }

}
