package one.microproject.rpi.powercontroller.tests;

import one.microproject.rpi.powercontroller.dto.ExecutionStatus;
import one.microproject.rpi.powercontroller.services.jobs.impl.ActionWait;
import one.microproject.rpi.powercontroller.tests.actions.ActionTask;
import one.microproject.rpi.powercontroller.tests.actions.DummyActionFail;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActionStateTests {

    private static ExecutorService executorService;

    @BeforeAll
    public static void init() {
        executorService = Executors.newSingleThreadExecutor();
    }

    @Test
    public void testActionOKStateNormalFlow() throws Exception {
        ActionWait okAction = new ActionWait(0,2L, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.WAITING, okAction.getStatus());
        executorService.submit(new ActionTask(okAction));
        okAction.awaitForStarted(10, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.IN_PROGRESS, okAction.getStatus());
        okAction.awaitForTermination(10, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.FINISHED, okAction.getStatus());
    }

    @Test
    public void testActionOKStateCancelFlow() throws Exception {
        ActionWait okAction = new ActionWait(0,0L, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.WAITING, okAction.getStatus());
        okAction.shutdown();
        okAction.awaitForTermination(10, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.CANCELLED, okAction.getStatus());
    }

    @Test
    public void testActionOKStateAbortFlow() throws Exception {
        ActionWait okAction = new ActionWait(0,10L, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.WAITING, okAction.getStatus());
        executorService.submit(new ActionTask(okAction));
        okAction.awaitForStarted(10, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.IN_PROGRESS, okAction.getStatus());
        okAction.shutdown();
        okAction.awaitForTermination(10, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.ABORTED, okAction.getStatus());
    }

    @Test
    public void testActionFailStateNormalFlow() throws Exception {
        DummyActionFail failAction = new DummyActionFail(0, 1L, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.WAITING, failAction.getStatus());
        executorService.submit(new ActionTask(failAction));
        failAction.awaitForStarted(10, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.IN_PROGRESS, failAction.getStatus());
        failAction.awaitForTermination(10, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.FAILED, failAction.getStatus());
    }

    @AfterAll
    public static void shutdown() {
        executorService.shutdown();
    }

}
