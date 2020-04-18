package itx.rpi.powercontroller.config.actions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.rpi.powercontroller.config.ActionConfiguration;

public class ActionPortHighConfig implements ActionConfiguration {

    private final Integer port;

    @JsonCreator
    public ActionPortHighConfig(@JsonProperty("port") Integer port) {
        this.port = port;
    }

    @Override
    public String getDescription() {
        return "Switch ON Port " + port;
    }

    @Override
    @JsonIgnore
    public Class<? extends ActionConfiguration> getType() {
        return ActionPortHighConfig.class;
    }

    public Integer getPort() {
        return port;
    }

}
