package itx.rpi.powercontroller.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "typeId")
public interface ActionConfiguration {

    @JsonIgnore
    String getDescription();

    @JsonIgnore
    Class<? extends ActionConfiguration> getType();

}
