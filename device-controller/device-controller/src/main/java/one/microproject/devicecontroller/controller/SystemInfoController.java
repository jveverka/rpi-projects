package one.microproject.devicecontroller.controller;

import one.microproject.devicecontroller.config.AppConfig;
import one.microproject.devicecontroller.dto.SystemInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/system/info")
public class SystemInfoController {

    private final AppConfig appConfig;

    public SystemInfoController(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @GetMapping
    ResponseEntity<SystemInfo> getSystemInfo() {
        return ResponseEntity.ok(new SystemInfo(appConfig.getId(), "1.1.1", "Device Controller"));
    }

}
