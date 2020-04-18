package itx.rpi.powercontroller.services.jobs;

import itx.rpi.powercontroller.dto.ActionInfo;

import java.util.Collection;

public class Job {

    private final String id;
    private final String name;
    private final Collection<Action> actions;

    public Job(String id, String name, Collection<Action> actions) {
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

    public Collection<Action> getActions() {
        return actions;
    }

}
