package one.microproject.rpi.camera.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CameraConfiguration {

    private final Float shutterSpeed;
    private final ImageFormat imageFormat;
    private final Resolution resolution;
    private final Rotation rotation;
    private final Integer quality;

    @JsonCreator
    public CameraConfiguration(@JsonProperty("shutterSpeed") Float shutterSpeed,
                               @JsonProperty("imageFormat") ImageFormat imageFormat,
                               @JsonProperty("resolution") Resolution resolution,
                               @JsonProperty("rotation") Rotation rotation,
                               @JsonProperty("quality") Integer quality) {
        this.shutterSpeed = shutterSpeed;
        this.imageFormat = imageFormat;
        this.resolution = resolution;
        this.rotation = rotation;
        this.quality = quality;
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

    public Integer getQuality() {
        return quality;
    }

    public static CameraConfiguration getDefault() {
        return new CameraConfiguration(0F, ImageFormat.JPEG, Resolution.M1, Rotation.D0, 85);
    }

    public static CameraConfiguration getMinimal() {
        return new CameraConfiguration(0F, ImageFormat.JPEG, Resolution.M03, Rotation.D0, 45);
    }

    private static class Builder {
        private Float shutterSpeed = 0F;
        private ImageFormat imageFormat = ImageFormat.JPEG;
        private Resolution resolution = Resolution.M1;
        private Rotation rotation =  Rotation.D0;
        private Integer quality = 85;

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

        public Builder setQuality(Integer quality) {
            if (quality < 0) {
                throw new UnsupportedOperationException("quality must be greater than 0 !");
            }
            if (quality > 100) {
                throw new UnsupportedOperationException("quality must be less than 100 !");
            }
            this.quality = quality;
            return this;
        }

        public CameraConfiguration build() {
            return new CameraConfiguration(shutterSpeed, imageFormat, resolution, rotation, quality);
        }

    }

}
