package one.microproject.rpi.powercontroller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Map;

public class SystemState {

    private final Date timeStamp;
    private final Map<Integer, Boolean> ports;
    private final Map<Integer, PortMapping> portMapping;

    @JsonCreator
    public SystemState(@JsonProperty("timeStamp") Date timeStamp,
                       @JsonProperty("ports") Map<Integer, Boolean> ports,
                       @JsonProperty("portMapping") Map<Integer, PortMapping> portMapping) {
        this.timeStamp = timeStamp;
        this.ports = ports;
        this.portMapping = portMapping;
    }

    public Map<Integer, Boolean> getPorts() {
        return ports;
    }

    public Map<Integer, PortMapping> getPortMapping() {
        return portMapping;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

}
