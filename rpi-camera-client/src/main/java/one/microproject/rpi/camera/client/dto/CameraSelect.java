package one.microproject.rpi.camera.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CameraSelect {

    private final Integer camera;

    @JsonCreator
    public CameraSelect(@JsonProperty("camera") Integer camera) {
        this.camera = camera;
    }

    public Integer getCamera() {
        return camera;
    }

}
