package one.microproject.devicecontroller.controller;

import one.microproject.devicecontroller.dto.DeviceCreateRequest;
import one.microproject.devicecontroller.dto.DeviceInfo;
import one.microproject.devicecontroller.service.DeviceAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/admin/devices")
public class DeviceAdminController {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceAdminController.class);

    private final DeviceAdminService deviceAdminService;

    public DeviceAdminController(DeviceAdminService deviceAdminService) {
        this.deviceAdminService = deviceAdminService;
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('device-controller.devices.read') and hasAuthority('device-controller.devices.write')")
    public ResponseEntity<Void> addDevice(@RequestBody DeviceCreateRequest deviceCreateRequest) {
        LOG.debug("addDevice {}", deviceCreateRequest.id());
        deviceAdminService.addDevice(deviceCreateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{device-id}")
    @PreAuthorize("hasAuthority('device-controller.devices.read') and hasAuthority('device-controller.devices.write')")
    public ResponseEntity<Void> removeDevice(@PathVariable("device-id") String deviceId) {
        LOG.debug("removeDevice {}", deviceId);
        deviceAdminService.removeDevice(deviceId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('device-controller.devices.read')")
    public ResponseEntity<List<DeviceInfo>> getAll() {
        LOG.debug("getAll");
        return ResponseEntity.ok(deviceAdminService.getAll());
    }

    @GetMapping("/{device-id}")
    @PreAuthorize("hasAuthority('device-controller.devices.read')")
    public ResponseEntity<DeviceInfo> getById(@PathVariable("device-id") String deviceId) {
        LOG.debug("getById {}", deviceId);
        return ResponseEntity.of(deviceAdminService.getById(deviceId));
    }

    @GetMapping("/device-types")
    @PreAuthorize("hasAuthority('device-controller.devices.read')")
    public ResponseEntity<List<String>> getSupportedDeviceTypes() {
        LOG.debug("getSupportedDeviceTypes");
        return ResponseEntity.ok(deviceAdminService.getSupportedDeviceTypes());
    }

}
