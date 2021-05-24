package one.microproject.devicecontroller.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;

public record DeviceQuery(String id, String deviceId, String queryType, ObjectNode payload) {
}
