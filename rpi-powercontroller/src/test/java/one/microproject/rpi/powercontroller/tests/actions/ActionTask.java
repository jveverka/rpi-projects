package one.microproject.rpi.powercontroller.tests.actions;

import one.microproject.rpi.powercontroller.services.jobs.Action;

public class ActionTask implements Runnable {

    private final Action action;

    public ActionTask(Action action) {
        this.action = action;
    }

    @Override
    public void run() {
        try {
            action.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
