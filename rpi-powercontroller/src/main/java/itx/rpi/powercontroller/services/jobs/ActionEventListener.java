package itx.rpi.powercontroller.services.jobs;

public interface ActionEventListener {

    void onActionStateChange(Integer ordinal, ExecutionStatus state);

}
