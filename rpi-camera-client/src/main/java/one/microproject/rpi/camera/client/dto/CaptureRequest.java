package one.microproject.rpi.camera.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CaptureRequest {

    private final Float shutterSpeed;
    private final ImageFormat imageFormat;
    private final Resolution resolution;
    private final Rotation rotation;

    @JsonCreator
    public CaptureRequest(@JsonProperty("shutterSpeed") Float shutterSpeed,
                          @JsonProperty("imageFormat") ImageFormat imageFormat,
                          @JsonProperty("resolution") Resolution resolution,
                          @JsonProperty("rotation") Rotation rotation) {
        this.shutterSpeed = shutterSpeed;
        this.imageFormat = imageFormat;
        this.resolution = resolution;
        this.rotation = rotation;
    }

    public Float getShutterSpeed() {
        return shutterSpeed;
    }

    public ImageFormat getImageFormat() {
        return imageFormat;
    }

    public Resolution getResolution() {
        return resolution;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public static CaptureRequest getDefault() {
        return new CaptureRequest(null, null, null, null);
    }

    private static class Builder {
        private Float shutterSpeed;
        private ImageFormat imageFormat;
        private Resolution resolution;
        private Rotation rotation;

        public Builder setShutterSpeed(Float shutterSpeed) {
            if (shutterSpeed < 0) {
                throw new UnsupportedOperationException("shutterSpeed must be greater than 0 !");
            }
            if (shutterSpeed > 10000) {
                throw new UnsupportedOperationException("shutterSpeed must be less than 10s !");
            }
            this.shutterSpeed = shutterSpeed;
            return this;
        }

        public Builder setImageFormat(ImageFormat imageFormat) {
            this.imageFormat = imageFormat;
            return this;
        }

        public Builder setResolution(Resolution resolution) {
            this.resolution = resolution;
            return this;
        }

        public Builder setRotation(Rotation rotation) {
            this.rotation = rotation;
            return this;
        }

        public CaptureRequest build() {
            return new CaptureRequest(shutterSpeed, imageFormat, resolution, rotation);
        }

    }

}
