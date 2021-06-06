package one.microproject.devicecontroller.controller;

import one.microproject.devicecontroller.config.AppConfig;
import one.microproject.rpi.device.dto.SystemInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.TimeZone;

@RestController
@RequestMapping(path = "/api/system/info")
public class SystemInfoController {

    private final AppConfig appConfig;
    private final Long started;

    public SystemInfoController(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.started = Instant.now().getEpochSecond();
    }

    @GetMapping
    ResponseEntity<SystemInfo<Void>> getSystemInfo() {
        Long timestamp = Instant.now().getEpochSecond();
        Long uptime = timestamp - started;
        String timeZone = TimeZone.getDefault().getID();
        return ResponseEntity.ok(new SystemInfo<>(appConfig.getId(), "device-controller", "1.4.2", "Device Controller [" + timeZone + "]", timestamp, uptime, null));
    }

}
