package itx.rpi.powercontroller.tests;

import itx.rpi.powercontroller.dto.TaskId;
import itx.rpi.powercontroller.services.jobs.ExecutionStatus;
import itx.rpi.powercontroller.services.jobs.Task;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskStateTests {

    @Test
    public void testNormalTaskExecutionFlow() {
        Task task = new Task(TaskId.from("task-001"), "job-001", "", Collections.emptyList(), new Date());
        assertEquals(ExecutionStatus.WAITING, task.getStatus());
        task.run();
        assertEquals(ExecutionStatus.FINISHED, task.getStatus());
        task.shutdown();
        assertEquals(ExecutionStatus.FINISHED, task.getStatus());
    }

    @Test
    public void testCancelWaitingTask() {
        Task task = new Task(TaskId.from("task-001"), "job-001", "", Collections.emptyList(), new Date());
        assertEquals(ExecutionStatus.WAITING, task.getStatus());
        task.shutdown();
        assertEquals(ExecutionStatus.CANCELLED, task.getStatus());
        task.run();
        assertEquals(ExecutionStatus.CANCELLED, task.getStatus());
        task.shutdown();
        assertEquals(ExecutionStatus.CANCELLED, task.getStatus());
    }

}
