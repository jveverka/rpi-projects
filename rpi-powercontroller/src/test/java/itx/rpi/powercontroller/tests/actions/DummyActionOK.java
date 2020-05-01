package itx.rpi.powercontroller.tests.actions;

import itx.rpi.powercontroller.services.jobs.ActionParent;

import java.util.concurrent.TimeUnit;

public class DummyActionOK extends ActionParent {

    private final Long delay;
    private final TimeUnit timeUnit;

    public DummyActionOK(Integer ordinal, Long delay, TimeUnit timeUnit) {
        super(ordinal);
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    @Override
    public String getType() {
        return "DummyActionOK";
    }

    @Override
    public String getDescription() {
        return "Dummy action always OK.";
    }

    @Override
    protected void taskBody() throws Exception {
        if (delay > 0) {
            int delayMs = (int)timeUnit.toMillis(delay);
            for (int i=0; i<delayMs; i++) {
                Thread.sleep(1);
                if (isStopped()) {
                    break;
                }
            }
        }
    }

}
