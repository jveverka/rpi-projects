package one.microproject.rpi.powercontroller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class JobInfo {

    private final String id;
    private final String name;
    private final Collection<ActionInfo> actions;

    @JsonCreator
    public JobInfo(@JsonProperty("id") String id,
                   @JsonProperty("name") String name,
                   @JsonProperty("actions") Collection<ActionInfo> actions) {
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

    public Collection<ActionInfo> getActions() {
        return actions;
    }

}
