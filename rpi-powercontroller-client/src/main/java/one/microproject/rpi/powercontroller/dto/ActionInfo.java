package one.microproject.rpi.powercontroller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ActionInfo {

    private final Integer ordinal;
    private final String type;
    private final String description;

    @JsonCreator
    public ActionInfo(@JsonProperty("ordinal") Integer ordinal,
                      @JsonProperty("type") String type,
                      @JsonProperty("description") String description) {
        this.ordinal = ordinal;
        this.type = type;
        this.description = description;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

}
