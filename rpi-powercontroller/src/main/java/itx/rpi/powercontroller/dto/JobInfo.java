package itx.rpi.powercontroller.dto;

import java.util.Collection;

public class JobInfo {

    private final String id;
    private final String name;
    private final Collection<ActionInfo> actions;

    public JobInfo(String id, String name, Collection<ActionInfo> actions) {
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
