package one.microproject.rpi.powercontroller.services.jobs;

import one.microproject.rpi.powercontroller.config.ActionConfiguration;

import java.util.Collection;

public class Job {

    private final String id;
    private final String name;
    private final Collection<ActionConfiguration> actions;

    public Job(String id, String name, Collection<ActionConfiguration> actions) {
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
