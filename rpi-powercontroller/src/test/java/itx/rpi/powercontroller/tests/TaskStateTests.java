package itx.rpi.powercontroller.tests;

import itx.rpi.powercontroller.dto.TaskId;
import itx.rpi.powercontroller.services.jobs.Action;
import itx.rpi.powercontroller.services.jobs.ExecutionStatus;
import itx.rpi.powercontroller.services.jobs.Task;
import itx.rpi.powercontroller.tests.actions.DummyActionFail;
import itx.rpi.powercontroller.tests.actions.DummyActionOK;
import itx.rpi.powercontroller.tests.actions.TaskEventListenerImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskStateTests {

    private static ExecutorService executorService;

    @BeforeAll
    public static void init() {
        executorService = Executors.newSingleThreadExecutor();
    }

    @Test
    public void testNormalTaskExecutionFlow() throws InterruptedException {
        TaskEventListenerImpl taskEventListener = new TaskEventListenerImpl();
        Task task = new Task(TaskId.from("task-001"), "job-001", "", Collections.emptyList(), new Date(), taskEventListener);
        taskEventListener.waitForWaiting(3L, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.WAITING, task.getStatus());
        task.run();
        taskEventListener.waitForFinished(3L, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.FINISHED, task.getStatus());
        task.shutdown();
        assertEquals(ExecutionStatus.FINISHED, task.getStatus());
        assertTrue(taskEventListener.checkExecutionStateOrder(
                ExecutionStatus.WAITING,
                ExecutionStatus.IN_PROGRESS,
                ExecutionStatus.FINISHED));
    }

    @Test
    public void testCancelWaitingTask() throws InterruptedException {
        TaskEventListenerImpl taskEventListener = new TaskEventListenerImpl();
        Task task = new Task(TaskId.from("task-001"), "job-001", "", Collections.emptyList(), new Date(), taskEventListener);
        taskEventListener.waitForWaiting(3L, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.WAITING, task.getStatus());
        task.shutdown();
        taskEventListener.waitForCancelled(3L, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.CANCELLED, task.getStatus());
        task.run();
        assertEquals(ExecutionStatus.CANCELLED, task.getStatus());
        task.shutdown();
        assertEquals(ExecutionStatus.CANCELLED, task.getStatus());
        assertTrue(taskEventListener.checkExecutionStateOrder(
                ExecutionStatus.WAITING,
                ExecutionStatus.CANCELLED));
    }

    @Test
    public void testCancelInProgressTask() throws InterruptedException {
        TaskEventListenerImpl taskEventListener = new TaskEventListenerImpl();
        Collection<Action> actions = new ArrayList<>();
        actions.add(new DummyActionOK(10L, TimeUnit.SECONDS));
        Task task = new Task(TaskId.from("task-001"), "job-001", "", actions, new Date(), taskEventListener);
        taskEventListener.waitForWaiting(3L, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.WAITING, task.getStatus());
        executorService.submit(task);
        taskEventListener.waitForInProgress(3L, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.IN_PROGRESS, task.getStatus());
        task.shutdown();
        taskEventListener.waitForAborted(3L, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.ABORTED, task.getStatus());
        assertTrue(taskEventListener.checkExecutionStateOrder(
                ExecutionStatus.WAITING,
                ExecutionStatus.IN_PROGRESS,
                ExecutionStatus.ABORTED));
    }

    @Test
    public void testFailedTaskExecutionFlow() throws InterruptedException {
        TaskEventListenerImpl taskEventListener = new TaskEventListenerImpl();
        Collection<Action> actions = new ArrayList<>();
        actions.add(new DummyActionFail());
        Task task = new Task(TaskId.from("task-001"), "job-001", "", actions, new Date(), taskEventListener);
        taskEventListener.waitForWaiting(3L, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.WAITING, task.getStatus());
        task.run();
        taskEventListener.waitForFailed(3L, TimeUnit.SECONDS);
        assertEquals(ExecutionStatus.FAILED, task.getStatus());
        task.shutdown();
        assertEquals(ExecutionStatus.FAILED, task.getStatus());
        assertTrue(taskEventListener.checkExecutionStateOrder(
                ExecutionStatus.WAITING,
                ExecutionStatus.IN_PROGRESS,
                ExecutionStatus.FAILED));
    }

    @AfterAll
    public static void shutdown() {
        executorService.shutdown();
    }

}
