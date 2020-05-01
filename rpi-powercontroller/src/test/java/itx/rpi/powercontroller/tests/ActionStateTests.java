package itx.rpi.powercontroller.tests;

import itx.rpi.powercontroller.services.jobs.ExecutionStatus;
import itx.rpi.powercontroller.tests.actions.DummyActionFail;
import itx.rpi.powercontroller.tests.actions.DummyActionOK;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActionStateTests {

    @Test
    public void testActionOKStateNormalFlow() throws Exception {
        DummyActionOK okAction = new DummyActionOK(0,0L, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.WAITING, okAction.getStatus());
        okAction.execute();
        assertEquals(ExecutionStatus.FINISHED, okAction.getStatus());
    }

    @Test
    public void testActionOKStateCancelFlow() throws Exception {
        DummyActionOK okAction = new DummyActionOK(0,0L, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.WAITING, okAction.getStatus());
        okAction.shutdown();
        assertEquals(ExecutionStatus.CANCELLED, okAction.getStatus());
    }

    @Test
    public void testActionFailStateNormalFlow() {
        DummyActionFail failAction = new DummyActionFail(0);
        assertEquals(ExecutionStatus.WAITING, failAction.getStatus());
        try {
            failAction.execute();
        } catch (Exception e) {
        }
        assertEquals(ExecutionStatus.FAILED, failAction.getStatus());
    }

}
