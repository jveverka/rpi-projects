package one.microproject.devicecontroller.controller;

import one.microproject.devicecontroller.dto.SystemInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/system/info")
public class SystemInfoController {

    @GetMapping
    ResponseEntity<SystemInfo> getSystemInfo() {
        return ResponseEntity.ok(new SystemInfo("device-controller-001", "1.0.0", "Device Controller"));
    }

}
