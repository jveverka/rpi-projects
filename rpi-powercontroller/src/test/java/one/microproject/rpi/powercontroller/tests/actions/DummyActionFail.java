package one.microproject.rpi.powercontroller.tests.actions;

import one.microproject.rpi.powercontroller.services.jobs.ActionParent;

import java.util.concurrent.TimeUnit;


public class DummyActionFail extends ActionParent {

    private final Long delay;
    private final TimeUnit timeUnit;

    public DummyActionFail(Integer ordinal, Long delay, TimeUnit timeUnit) {
        super(ordinal);
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

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
        if (delay > 0) {
            int delayMs = (int)timeUnit.toMillis(delay);
            for (int i=0; i<delayMs; i++) {
                Thread.sleep(1);
                if (isStopped()) {
                    break;
                }
            }
        }
        throw new UnsupportedOperationException("");
    }

}
