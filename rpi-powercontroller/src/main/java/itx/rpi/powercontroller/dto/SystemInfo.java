package itx.rpi.powercontroller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class SystemInfo {

    private final String id;
    private final String type;
    private final String name;
    private final String version;
    private final boolean hardware;
    private final Date started;
    private final long uptime;
    private final String uptimeDays;

    @JsonCreator
    public SystemInfo(@JsonProperty("id") String id,
                      @JsonProperty("type") String type,
                      @JsonProperty("name") String name,
                      @JsonProperty("version") String version,
                      @JsonProperty("hardware") boolean hardware,
                      @JsonProperty("started") Date started,
                      @JsonProperty("uptime") long uptime,
                      @JsonProperty("uptimeDays") String uptimeDays) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.version = version;
        this.hardware = hardware;
        this.started = started;
        this.uptime = uptime;
        this.uptimeDays = uptimeDays;
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

    public Date getStarted() {
        return started;
    }

    public long getUptime() {
        return uptime;
    }

    public String getUptimeDays() {
        return uptimeDays;
    }
}
