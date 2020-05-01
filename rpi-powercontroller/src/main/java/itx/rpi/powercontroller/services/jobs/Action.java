package itx.rpi.powercontroller.services.jobs;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public interface Action {

    Integer getOrdinal();

    String getType();

    String getDescription();

    Date getStarted();

    Long getDuration();

    ExecutionStatus getStatus();

    void execute() throws Exception;

    void shutdown();

    boolean awaitForStarted(long timeout, TimeUnit duration) throws InterruptedException;

    boolean awaitForTermination(long timeout, TimeUnit duration) throws InterruptedException;

}
