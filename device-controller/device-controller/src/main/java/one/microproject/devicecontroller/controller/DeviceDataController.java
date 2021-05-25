package one.microproject.devicecontroller.controller;

import one.microproject.devicecontroller.dto.DeviceInfo;
import one.microproject.devicecontroller.dto.DeviceQuery;
import one.microproject.devicecontroller.dto.DeviceQueryResponse;
import one.microproject.devicecontroller.service.DeviceAdminService;
import one.microproject.devicecontroller.service.DeviceDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping(path = "/api/data/devices")
public class DeviceDataController {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceDataController.class);

    private final DeviceAdminService deviceAdminService;
    private final DeviceDataService deviceDataService;

    public DeviceDataController(DeviceAdminService deviceAdminService, DeviceDataService deviceDataService) {
        this.deviceAdminService = deviceAdminService;
        this.deviceDataService = deviceDataService;
    }

    @GetMapping
    public ResponseEntity<List<DeviceInfo>> getAll() {
        LOG.debug("getAll");
        return ResponseEntity.ok(deviceAdminService.getAll());
    }

    @GetMapping("/{device-id}")
    public ResponseEntity<DeviceInfo> getById(@PathVariable("device-id") String deviceId) {
        LOG.debug("getById {}", deviceId);
        return ResponseEntity.of(deviceAdminService.getById(deviceId));
    }

    @PostMapping("/query")
    public ResponseEntity<DeviceQueryResponse> query(@RequestBody DeviceQuery query) {
        return ResponseEntity.ok(deviceDataService.query(query));
    }

    @PostMapping("/download")
    public ResponseEntity<Resource> downloadQuery(@RequestBody DeviceQuery query) {
        InputStream is = deviceDataService.download(query);
        InputStreamResource resource = new InputStreamResource(is);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/jpeg"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"filename=capture-image.jpg\"")
                .body(resource);
    }

}
