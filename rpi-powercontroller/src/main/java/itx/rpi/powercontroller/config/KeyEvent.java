package itx.rpi.powercontroller.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class KeyEvent {

    private final Integer port;
    private final Boolean toggle;
    private final String toggleOnJob;
    private final String toggleOffJob;

    @JsonCreator
    public KeyEvent(@JsonProperty("port") Integer port,
                    @JsonProperty("toggle") Boolean toggle,
                    @JsonProperty("toggleOnJob") String toggleOnJob,
                    @JsonProperty("toggleOffJob") String toggleOffJob) {
        this.port = port;
        this.toggle = toggle;
        this.toggleOnJob = toggleOnJob;
        this.toggleOffJob = toggleOffJob;
    }

    public Integer getPort() {
        return port;
    }

    public Boolean getToggle() {
        return toggle;
    }

    public String getToggleOnJob() {
        return toggleOnJob;
    }

    public String getToggleOffJob() {
        return toggleOffJob;
    }

}
