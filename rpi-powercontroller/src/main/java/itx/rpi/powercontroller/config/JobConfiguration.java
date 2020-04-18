package itx.rpi.powercontroller.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class JobConfiguration {

    private final String id;
    private final String name;
    private final Collection<ActionConfiguration> actions;

    @JsonCreator
    public JobConfiguration(@JsonProperty("id") String id,
                            @JsonProperty("name") String name,
                            @JsonProperty("actions") Collection<ActionConfiguration> actions) {
        this.id = id;
        this.name = name;
        this.actions = actions;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Collection<ActionConfiguration> getActions() {
        return actions;
    }

}
