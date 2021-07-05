package one.microproject.rpi.camera.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CameraConfiguration {

    private final String resolution;
    private final String rotation;
    private final Integer framerate;

    @JsonCreator
    public CameraConfiguration(@JsonProperty("resolution") String resolution,
                               @JsonProperty("rotation") String rotation,
                               @JsonProperty("framerate") Integer framerate) {
        this.resolution = resolution;
        this.rotation = rotation;
        this.framerate = framerate;
    }

    public String getResolution() {
        return resolution;
    }

    public String getRotation() {
        return rotation;
    }

    public Integer getFramerate() {
        return framerate;
    }

    public static CameraConfiguration getDefault() {
        return new CameraConfiguration("R1", "D0", 24);
    }

    private static class Builder {
        private String resolution = "R1";
        private String rotation =  "D0";
        private Integer framerate = 24;

        public Builder setResolution(String resolution) {
            this.resolution = resolution;
            return this;
        }

        public Builder setRotation(String rotation) {
            this.rotation = rotation;
            return this;
        }

        public Builder setFrameRate(Integer framerate) {
            if (framerate < 0) {
                throw new UnsupportedOperationException("quality must be greater than 0 !");
            }
            if (framerate > 100) {
                throw new UnsupportedOperationException("quality must be less than 100 !");
            }
            this.framerate = framerate;
            return this;
        }

        public CameraConfiguration build() {
            return new CameraConfiguration(resolution, rotation, framerate);
        }

    }

}
