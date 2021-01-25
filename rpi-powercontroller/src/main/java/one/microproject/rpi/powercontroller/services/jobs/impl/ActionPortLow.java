package one.microproject.rpi.powercontroller.services.jobs.impl;

import one.microproject.rpi.powercontroller.services.RPiService;
import one.microproject.rpi.powercontroller.services.jobs.ActionParent;

public class ActionPortLow extends ActionParent {

    private final Integer port;
    private final RPiService rPiService;

    public ActionPortLow(Integer ordinal, Integer port, RPiService rPiService) {
        super(ordinal);
        this.port = port;
        this.rPiService = rPiService;
   }

    @Override
    public String getType() {
        return "ActionPortLow";
    }

    @Override
    public String getDescription() {
        return "Switch OFF port " + port;
    }

    @Override
    public void taskBody() throws Exception {
        rPiService.setPortState(port, false);
    }

}
