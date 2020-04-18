package itx.rpi.powercontroller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class JobId extends Id {

    @JsonCreator
    public JobId(@JsonProperty("id") String id) {
        super(id);
    }

    public static JobId from(String id) {
        return new JobId(id);
    }

}
