package itx.rpi.powercontroller.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Map;

public class Configuration {

    private final String id;
    private final String name;
    private final String host;
    private final int port;
    private final boolean hardware;
    private final Map<Integer, PortType> portTypes;
    private final Collection<JobConfiguration> jobConfigurations;

    @JsonCreator
    public Configuration(@JsonProperty("id") String id,
                         @JsonProperty("name") String name,
                         @JsonProperty("host") String host,
                         @JsonProperty("port") int port,
                         @JsonProperty("hardware") boolean hardware,
                         @JsonProperty("portTypes") Map<Integer, PortType> portTypes,
                         @JsonProperty("jobConfigurations") Collection<JobConfiguration> jobConfigurations) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.port = port;
        this.hardware = hardware;
        this.portTypes = portTypes;
        this.jobConfigurations = jobConfigurations;
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

    public Map<Integer, PortType> getPortsTypes() {
        return portTypes;
    }

    public Collection<JobConfiguration> getJobConfigurations() {
        return jobConfigurations;
    }

}
