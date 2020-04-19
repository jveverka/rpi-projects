package itx.rpi.powercontroller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ActionInfo {

    private final String type;
    private final String description;

    @JsonCreator
    public ActionInfo(@JsonProperty("type") String type,
                      @JsonProperty("description") String description) {
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

}
