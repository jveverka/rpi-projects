package itx.rpi.powercontroller.services.jobs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class KeyEvent {

    private final Integer port;
    private final String toggleOnJob;
    private final String toggleOffJob;

    @JsonCreator
    public KeyEvent(@JsonProperty("port") Integer port,
                    @JsonProperty("toggleOnJob") String toggleOnJob,
                    @JsonProperty("toggleOffJob") String toggleOffJob) {
        this.port = port;
        this.toggleOnJob = toggleOnJob;
        this.toggleOffJob = toggleOffJob;
    }

    public Integer getPort() {
        return port;
    }

    public String getToggleOnJob() {
        return toggleOnJob;
    }

    public String getToggleOffJob() {
        return toggleOffJob;
    }

}
