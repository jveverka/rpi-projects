package itx.rpi.powercontroller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class SystemState {

    private final Map<Integer, Boolean> ports;

    @JsonCreator
    public SystemState(@JsonProperty("ports") Map<Integer, Boolean> ports) {
        this.ports = ports;
    }

    public Map<Integer, Boolean> getPorts() {
        return ports;
    }

}
