package one.microproject.rpi.powercontroller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SetPortRequest {

    private final Integer port;
    private final Boolean state;

    @JsonCreator
    public SetPortRequest(@JsonProperty("port") Integer port,
                          @JsonProperty("state") Boolean state) {
        this.port = port;
        this.state = state;
    }

    public Integer getPort() {
        return port;
    }

    public Boolean getState() {
        return state;
    }
}
