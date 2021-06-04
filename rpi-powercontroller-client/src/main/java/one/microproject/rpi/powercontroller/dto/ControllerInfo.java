package one.microproject.rpi.powercontroller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class ControllerInfo {

    private final boolean hardware;
    private final Date started;
    private final String uptimeDays;

    @JsonCreator
    public ControllerInfo(@JsonProperty("hardware") boolean hardware,
                          @JsonProperty("started") Date started,
                          @JsonProperty("uptimeDays") String uptimeDays) {
        this.hardware = hardware;
        this.started = started;
        this.uptimeDays = uptimeDays;
    }

    public boolean isHardware() {
        return hardware;
    }

    public Date getStarted() {
        return started;
    }

    public String getUptimeDays() {
        return uptimeDays;
    }

}
