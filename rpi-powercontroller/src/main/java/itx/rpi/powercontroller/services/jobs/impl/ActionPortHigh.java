package itx.rpi.powercontroller.services.jobs.impl;

import itx.rpi.powercontroller.services.RPiService;
import itx.rpi.powercontroller.services.jobs.ActionParent;

public class ActionPortHigh extends ActionParent {

    private final Integer port;
    private final RPiService rPiService;

    public ActionPortHigh(Integer ordinal, Integer port, RPiService rPiService) {
        super(ordinal);
        this.port = port;
        this.rPiService = rPiService;
    }

    @Override
    public String getType() {
        return "ActionPortHigh";
    }

    @Override
    public String getDescription() {
        return "Switch ON port " + port;
    }

    @Override
    public void taskBody() throws Exception {
        rPiService.setPortState(port, true);
    }

}
