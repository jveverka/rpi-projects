package one.microproject.devicecontroller.dto;

public record DeviceCreateRequest(String id, String type, String baseUrl, DeviceCredentials credentials, String groupId) {}
