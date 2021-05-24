package one.microproject.devicecontroller.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;

public record DeviceQueryResponse(String id, String deviceId, String queryType, ObjectNode payload) {
}
