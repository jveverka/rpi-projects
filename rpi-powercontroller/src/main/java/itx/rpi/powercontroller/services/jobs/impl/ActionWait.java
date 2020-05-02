package itx.rpi.powercontroller.services.jobs.impl;

import itx.rpi.powercontroller.services.jobs.ActionParent;

import java.util.concurrent.TimeUnit;

public class ActionWait extends ActionParent {

    private final Long delay;
    private final TimeUnit timeUnit;

    public ActionWait(Integer ordinal, Long delay, TimeUnit timeUnit) {
        super(ordinal);
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    @Override
    public String getType() {
        return "ActionWait";
    }

    @Override
    public String getDescription() {
        return "Wait for " + delay + " " + timeUnit;
    }

    @Override
    public void taskBody() throws Exception {
        long milliseconds = timeUnit.toMillis(delay);
        if (milliseconds > 0) {
            for (int i=0; i<milliseconds; i++) {
                Thread.sleep(1);
                if (isStopped()) {
                    return;
                }
            }
        }
    }

}
