package one.microproject.rpi.camera.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CameraInfo {

    private final String revision;

    @JsonCreator
    public CameraInfo(@JsonProperty("revision") String revision) {
        this.revision = revision;
    }

    public String getRevision() {
        return revision;
    }

}
