package one.microproject.rpi.device.sim.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SimInfo {

    private final String status;

    @JsonCreator
    public SimInfo(@JsonProperty("status") String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
