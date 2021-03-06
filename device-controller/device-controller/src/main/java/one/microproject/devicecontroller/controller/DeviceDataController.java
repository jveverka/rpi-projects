package one.microproject.devicecontroller.controller;

import one.microproject.devicecontroller.dto.DataStream;
import one.microproject.devicecontroller.dto.DeviceQuery;
import one.microproject.devicecontroller.dto.DeviceQueryResponse;
import one.microproject.devicecontroller.service.DeviceDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/data/devices")
public class DeviceDataController {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceDataController.class);

    private final DeviceDataService deviceDataService;

    public DeviceDataController(DeviceDataService deviceDataService) {
        this.deviceDataService = deviceDataService;
    }

    @PostMapping("/query")
    @PreAuthorize("hasAuthority('device-controller.data.read') and hasAuthority('device-controller.data.write')")
    public ResponseEntity<DeviceQueryResponse> query(@RequestBody DeviceQuery query) {
        LOG.info("query {}", query.deviceId());
        return ResponseEntity.ok(deviceDataService.query(query));
    }

    @GetMapping("/query")
    @PreAuthorize("hasAuthority('device-controller.data.read') and hasAuthority('device-controller.data.write')")
    public ResponseEntity<DeviceQueryResponse> getQuery(@RequestParam String id, @RequestParam String deviceId,  @RequestParam String queryType) {
        DeviceQuery query = new DeviceQuery(id, deviceId, queryType, null);
        LOG.info("get query {}", query.deviceId());
        return ResponseEntity.ok(deviceDataService.query(query));
    }

    @PostMapping("/download")
    @PreAuthorize("hasAuthority('device-controller.data.read') and hasAuthority('device-controller.data.write')")
    public ResponseEntity<Resource> download(@RequestBody DeviceQuery query) {
        LOG.info("download {}", query.deviceId());
        DataStream dataStream = deviceDataService.download(query);
        InputStreamResource resource = new InputStreamResource(dataStream.is());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(dataStream.mimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dataStream.fileName() + "\"")
                .body(resource);
    }

    @GetMapping("/download")
    @PreAuthorize("hasAuthority('device-controller.data.read') and hasAuthority('device-controller.data.write')")
    public ResponseEntity<Resource> getDownload(@RequestParam String id, @RequestParam String deviceId,  @RequestParam String queryType) {
        DeviceQuery query = new DeviceQuery(id, deviceId, queryType, null);
        LOG.info("get download {}", query.deviceId());
        DataStream dataStream = deviceDataService.download(query);
        InputStreamResource resource = new InputStreamResource(dataStream.is());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(dataStream.mimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dataStream.fileName() + "\"")
                .body(resource);
    }

}
