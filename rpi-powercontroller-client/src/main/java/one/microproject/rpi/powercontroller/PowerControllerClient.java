package one.microproject.rpi.powercontroller;

import one.microproject.rpi.powercontroller.dto.JobId;
import one.microproject.rpi.powercontroller.dto.TaskId;

import java.util.Optional;

public interface PowerControllerClient extends PowerControllerReadClient {

    /**
     * Set OUTPUT port state (ON=true | OFF=false)
     * @param port output port index.
     * @param state required output port state.
     * @return resulting output port state.
     */
    boolean setPortState(Integer port, Boolean state);

    /**
     * Submit task for execution.
     * @param id {@link JobId} unique ID of jop to be submitted as new task.
     * @return unique {@link TaskId} of submitted task.
     */
    Optional<TaskId> submitTask(JobId id);

    /**
     * Cancel running task.
     * @param id unique {@link TaskId}.
     * @return True if the task has  been cancelled, false otherwise.
     */
    boolean cancelTask(TaskId id);

    /**
     * Cancel all running task.
     * @return True if action succeeded, False otherwise.
     */
    boolean cancelAllTasks();

    /**
     * Blocking wait for the starting task.
     * @param id unique {@link TaskId}.
     * @return True if action succeeded, False otherwise.
     */
    boolean waitForTaskStarted(TaskId id);

    /**
     * Blocking wait for the task termination.
     * @param id unique {@link TaskId}.
     * @return True if action succeeded, False otherwise.
     */
    boolean waitForTaskTermination(TaskId id);

    /**
     * Clean task queue, remove all tasks in terminal state.
     * @return True if action succeeded, False otherwise.
     */
    boolean cleanTaskQueue();

}
