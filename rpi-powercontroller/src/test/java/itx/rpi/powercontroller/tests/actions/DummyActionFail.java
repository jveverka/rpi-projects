package itx.rpi.powercontroller.tests.actions;

import itx.rpi.powercontroller.services.jobs.ActionParent;


public class DummyActionFail extends ActionParent {

    @Override
    public String getType() {
        return "DummyActionFail";
    }

    @Override
    public String getDescription() {
        return "Dummy action which always fails.";
    }

    @Override
    protected void taskBody() throws Exception {
        throw new UnsupportedOperationException("");
    }

}
