package itx.rpi.powercontroller.tests;

import itx.rpi.powercontroller.dto.JobId;
import itx.rpi.powercontroller.dto.TaskId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class BasicTests {

    @Test
    public void jobIdEqualsTest() {
        JobId jobIdA001 = JobId.from("job-001");
        JobId jobIdB001 = JobId.from("job-001");
        JobId jobIdA002 = JobId.from("job-002");
        assertEquals(jobIdA001, jobIdA001);
        assertEquals(jobIdB001, jobIdB001);
        assertEquals(jobIdA001, jobIdB001);
        assertEquals(jobIdB001, jobIdA001);
        assertNotEquals(jobIdA002, jobIdA001);
        assertNotEquals(jobIdA002, jobIdB001);
    }

    @Test
    public void taskIdEqualsTest() {
        TaskId taskIdA001 = TaskId.from("task-001");
        TaskId taskIdB001 = TaskId.from("task-001");
        TaskId taskIdA002 = TaskId.from("task-002");
        assertEquals(taskIdA001, taskIdA001);
        assertEquals(taskIdB001, taskIdB001);
        assertEquals(taskIdA001, taskIdB001);
        assertEquals(taskIdB001, taskIdA001);
        assertNotEquals(taskIdA002, taskIdA001);
        assertNotEquals(taskIdA002, taskIdB001);
    }

}
