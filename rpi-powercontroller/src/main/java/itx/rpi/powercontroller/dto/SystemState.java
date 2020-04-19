package itx.rpi.powercontroller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.rpi.powercontroller.config.PortType;

import java.util.Date;
import java.util.Map;

public class SystemState {

    private final Date timeStamp;
    private final Map<Integer, Boolean> ports;
    private final Map<Integer, PortType> portTypes;

    @JsonCreator
    public SystemState(@JsonProperty("timeStamp") Date timeStamp,
                       @JsonProperty("ports") Map<Integer, Boolean> ports,
                       @JsonProperty("portTypes") Map<Integer, PortType> portTypes) {
        this.timeStamp = timeStamp;
        this.ports = ports;
        this.portTypes = portTypes;
    }

    public Map<Integer, Boolean> getPorts() {
        return ports;
    }

    public Map<Integer, PortType> getPortTypes() {
        return portTypes;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

}
