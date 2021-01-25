package one.microproject.rpi.powercontroller.services.jobs;

import one.microproject.rpi.powercontroller.dto.ExecutionStatus;
import one.microproject.rpi.powercontroller.dto.JobId;
import one.microproject.rpi.powercontroller.dto.TaskId;

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

    boolean awaitForStarted(long timeout, TimeUnit duration) throws InterruptedException;

    boolean awaitForTermination(long timeout, TimeUnit duration) throws InterruptedException;

}
