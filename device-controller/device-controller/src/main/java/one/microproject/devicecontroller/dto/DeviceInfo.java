package one.microproject.devicecontroller.dto;

public record DeviceInfo(String id, String type, String groupId, String baseUrl, DeviceStatus status) {}
