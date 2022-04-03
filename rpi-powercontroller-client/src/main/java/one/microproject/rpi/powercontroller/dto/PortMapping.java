package one.microproject.rpi.powercontroller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PortMapping {

    private final int address;
    private final PortType type;

    @JsonCreator
    public PortMapping(@JsonProperty("address") int address,
                       @JsonProperty("type") PortType type) {
        this.address = address;
        this.type = type;
    }

    public int getAddress() {
        return address;
    }

    public PortType getType() {
        return type;
    }

}
