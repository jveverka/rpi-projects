package one.microproject.devicecontroller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@SpringBootApplication
public class HomeControllerApp {

    public static void main(String[] args) {
        SpringApplication.run(HomeControllerApp.class, args);
    }

}
