package one.microproject.rpi.powercontroller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskId extends Id {

    @JsonCreator
    protected TaskId(@JsonProperty("id") String id) {
        super(id);
    }

    public static TaskId from(String id) {
        return new TaskId(id);
    }

}
