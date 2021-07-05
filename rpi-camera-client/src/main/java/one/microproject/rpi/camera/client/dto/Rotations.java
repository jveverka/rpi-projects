package one.microproject.rpi.camera.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Rotations {

    private final List<String> rotations;

    @JsonCreator
    public Rotations(@JsonProperty("rotations") List<String> rotations) {
        this.rotations = rotations;
    }

    public List<String> getRotations() {
        return rotations;
    }

}
