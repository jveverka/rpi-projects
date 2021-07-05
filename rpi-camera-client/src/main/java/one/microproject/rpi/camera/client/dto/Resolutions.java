package one.microproject.rpi.camera.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Resolutions {

    private final Map<String, Resolution> resolutions;

    @JsonCreator
    public Resolutions(@JsonProperty("resolutions") Map<String, Resolution> resolutions) {
        this.resolutions = resolutions;
    }

    public Map<String, Resolution> getResolutions() {
        return resolutions;
    }

}
