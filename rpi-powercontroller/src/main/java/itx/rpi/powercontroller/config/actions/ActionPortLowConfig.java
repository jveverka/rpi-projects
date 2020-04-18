package itx.rpi.powercontroller.config.actions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.rpi.powercontroller.config.ActionConfiguration;

public class ActionPortLowConfig implements ActionConfiguration {

    private final Integer port;

    @JsonCreator
    public ActionPortLowConfig(@JsonProperty("port") Integer port) {
        this.port = port;
    }

    @Override
    @JsonIgnore
    public Class<? extends ActionConfiguration> getType() {
        return ActionPortLowConfig.class;
    }

    public Integer getPort() {
        return port;
    }

}
