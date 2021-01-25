package one.microproject.rpi.powercontroller.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class Configuration {

    private final String id;
    private final String name;
    private final String host;
    private final int port;
    private final boolean hardware;
    private final Map<Integer, PortType> portTypes;
    private final Collection<JobConfiguration> jobConfigurations;
    private final Date started;
    private final Map<String, String> credentials;
    private final Collection<String> executeJobsOnStart;
    private final Map<Integer, KeyEvent> keyEvents;
    private final String killAllTasksJobId;
    private final TaskQueueInterval taskQueueMaxAge;
    private final TaskQueueInterval taskQueueCleanupInterval;

    @JsonCreator
    public Configuration(@JsonProperty("id") String id,
                         @JsonProperty("name") String name,
                         @JsonProperty("host") String host,
                         @JsonProperty("port") int port,
                         @JsonProperty("hardware") boolean hardware,
                         @JsonProperty("portTypes") Map<Integer, PortType> portTypes,
                         @JsonProperty("jobConfigurations") Collection<JobConfiguration> jobConfigurations,
                         @JsonProperty("credentials") Map<String, String> credentials,
                         @JsonProperty("executeJobsOnStart") Collection<String> executeJobsOnStart,
                         @JsonProperty("keyEvents") Map<Integer, KeyEvent> keyEvents,
                         @JsonProperty("killAllTasksJobId") String killAllTasksJobId,
                         @JsonProperty("taskQueueMaxAge") TaskQueueInterval taskQueueMaxAge,
                         @JsonProperty("taskQueueCleanupInterval") TaskQueueInterval taskQueueCleanupInterval) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.port = port;
        this.hardware = hardware;
        this.portTypes = portTypes;
        this.jobConfigurations = jobConfigurations;
        this.started = new Date();
        this.credentials = credentials;
        this.executeJobsOnStart = executeJobsOnStart;
        this.keyEvents = keyEvents;
        this.killAllTasksJobId = killAllTasksJobId;
        this.taskQueueMaxAge = taskQueueMaxAge;
        this.taskQueueCleanupInterval = taskQueueCleanupInterval;
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

    @JsonIgnore
    public Date getStarted() {
        return started;
    }

    public Map<String, String> getCredentials() {
        return credentials;
    }

    public Collection<String> getExecuteJobsOnStart() {
        return executeJobsOnStart;
    }

    public Map<Integer, KeyEvent> getKeyEvents() {
        return keyEvents;
    }

    public String getKillAllTasksJobId() {
        return killAllTasksJobId;
    }

    public TaskQueueInterval getTaskQueueMaxAge() {
        return taskQueueMaxAge;
    }

    public TaskQueueInterval getTaskQueueCleanupInterval() {
        return taskQueueCleanupInterval;
    }
}
