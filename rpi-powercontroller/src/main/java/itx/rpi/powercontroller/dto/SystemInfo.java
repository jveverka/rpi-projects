package itx.rpi.powercontroller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemInfo {

    private final String id;
    private final String type;
    private final String name;
    private final String version;
    private final boolean hardware;

    @JsonCreator
    public SystemInfo(@JsonProperty("id") String id,
                      @JsonProperty("type") String type,
                      @JsonProperty("name") String name,
                      @JsonProperty("version") String version,
                      @JsonProperty("hardware") boolean hardware) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.version = version;
        this.hardware = hardware;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public boolean isHardware() {
        return hardware;
    }

}
