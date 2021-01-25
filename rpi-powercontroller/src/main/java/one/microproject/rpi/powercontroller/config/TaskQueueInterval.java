package one.microproject.rpi.powercontroller.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.concurrent.TimeUnit;

public class TaskQueueInterval {

    private final Long duration;
    private final TimeUnit timeUnit;

    @JsonCreator
    public TaskQueueInterval(@JsonProperty("duration") Long duration,
                             @JsonProperty("timeUnit") TimeUnit timeUnit) {
        this.duration = duration;
        this.timeUnit = timeUnit;
    }

    public Long getDuration() {
        return duration;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

}
