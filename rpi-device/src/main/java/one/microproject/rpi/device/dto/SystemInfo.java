package one.microproject.rpi.device.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemInfo<T> {

    private final String id;
    private final String type;
    private final String version;
    private final String name;
    private final Long timestamp;
    private final Long uptime;
    private final T properties;

    @JsonCreator
    public SystemInfo(@JsonProperty("id") String id,
                      @JsonProperty("type") String type,
                      @JsonProperty("version") String version,
                      @JsonProperty("name") String name,
                      @JsonProperty("timestamp") Long timestamp,
                      @JsonProperty("uptime") Long uptime,
                      @JsonProperty("properties") T properties) {
        this.id = id;
        this.type = type;
        this.version = version;
        this.name = name;
        this.timestamp = timestamp;
        this.uptime = uptime;
        this.properties = properties;
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

    public Long getTimestamp() {
        return timestamp;
    }

    public Long getUptime() {
        return uptime;
    }

    public T getProperties() {
        return properties;
    }

}
