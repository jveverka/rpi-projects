package itx.rpi.powercontroller.services.jobs.impl;

import itx.rpi.powercontroller.services.jobs.ActionParent;

import java.util.concurrent.TimeUnit;

public class ActionWait extends ActionParent {

    private final Long delay;
    private final TimeUnit timeUnit;

    public ActionWait(Long delay, TimeUnit timeUnit) {
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
        long ticks = milliseconds / 100;
        long reminder = milliseconds - ticks * 100;
        if (ticks <= 0) {
            Thread.sleep(milliseconds);
        } else {
            for (int i = 0; i < ticks; i++) {
                Thread.sleep(100);
                if (isStopped()) {
                    return;
                }
            }
            Thread.sleep(reminder);
        }
    }

}
