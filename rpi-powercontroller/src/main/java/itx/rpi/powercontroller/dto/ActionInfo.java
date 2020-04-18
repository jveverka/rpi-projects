package itx.rpi.powercontroller.dto;

public class ActionInfo {

    private final String type;
    private final String description;

    public ActionInfo(String type, String description) {
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
