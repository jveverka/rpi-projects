package one.microproject.rpi.camera.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Resolution {

    private final Integer height;
    private final Integer width;

    @JsonCreator
    public Resolution(@JsonProperty("height") Integer height,
                      @JsonProperty("width") Integer width) {
        this.height = height;
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWidth() {
        return width;
    }

}
