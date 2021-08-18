package one.microproject.devicecontroller.controller;

import one.microproject.devicecontroller.config.AppConfig;
import one.microproject.devicecontroller.dto.DCInfo;
import one.microproject.rpi.device.dto.SystemInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.TimeZone;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/system/info")
public class SystemInfoController {

    private final AppConfig appConfig;
    private final Long started;
    private final String instanceId;

    public SystemInfoController(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.started = Instant.now().getEpochSecond();
        this.instanceId = UUID.randomUUID().toString();
    }

    @GetMapping
    ResponseEntity<SystemInfo<DCInfo>> getSystemInfo() {
        Long timestamp = Instant.now().getEpochSecond();
        Long uptime = timestamp - started;
        String timeZone = TimeZone.getDefault().getID();
        DCInfo dcInfo = new DCInfo(instanceId);
        return ResponseEntity.ok(new SystemInfo<>(appConfig.getId(), "device-controller", "1.4.21", "Device Controller [" + timeZone + "]", timestamp, uptime, dcInfo));
    }

}
