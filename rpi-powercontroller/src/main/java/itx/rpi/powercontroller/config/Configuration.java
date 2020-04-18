package itx.rpi.powercontroller.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Configuration {

    private final String id;
    private final String name;
    private final String host;
    private final int port;
    private final boolean hardware;

    @JsonCreator
    public Configuration(@JsonProperty("id") String id,
                         @JsonProperty("name") String name,
                         @JsonProperty("host") String host,
                         @JsonProperty("port") int port,
                         @JsonProperty("hardware") boolean hardware) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.port = port;
        this.hardware = hardware;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isHardware() {
        return hardware;
    }

}
