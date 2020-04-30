package itx.rpi.powercontroller.services.jobs;

import itx.rpi.powercontroller.dto.JobId;
import itx.rpi.powercontroller.dto.TaskId;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public interface Task {

    TaskId getId();

    JobId getJobId();

    String getJobName();

    ExecutionStatus getStatus();

    Date getSubmitted();

    Collection<Action> getActions();

    Date getStarted();

    Long getDuration();

    void shutdown();

    boolean await(long timeout, TimeUnit duration) throws InterruptedException;

}
