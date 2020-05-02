package itx.rpi.powercontroller.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class KeyEvent {

    private final Integer port;
    private final EventType type;
    private final EventTrigger trigger;
    private final String toggleOnJob;
    private final String toggleOffJob;

    @JsonCreator
    public KeyEvent(@JsonProperty("port") Integer port,
                    @JsonProperty("type") EventType type,
                    @JsonProperty("trigger") EventTrigger trigger,
                    @JsonProperty("toggleOnJob") String toggleOnJob,
                    @JsonProperty("toggleOffJob") String toggleOffJob) {
        this.port = port;
        this.type = type;
        this.trigger = trigger;
        this.toggleOnJob = toggleOnJob;
        this.toggleOffJob = toggleOffJob;
    }

    public Integer getPort() {
        return port;
    }

    public EventType getType() {
        return type;
    }

    public EventTrigger getTrigger() {
        return trigger;
    }

    public String getToggleOnJob() {
        return toggleOnJob;
    }

    public String getToggleOffJob() {
        return toggleOffJob;
    }

}
