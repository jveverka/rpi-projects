package one.microproject.rpi.camera.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemInfo {

    private final String id;
    private final String type;
    private final String version;
    private final String name;

    @JsonCreator
    public SystemInfo(@JsonProperty("id") String id,
                      @JsonProperty("type") String type,
                      @JsonProperty("version") String version,
                      @JsonProperty("name") String name) {
        this.id = id;
        this.type = type;
        this.version = version;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

}
