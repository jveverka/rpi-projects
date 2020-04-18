package itx.rpi.powercontroller.services;

import itx.rpi.powercontroller.dto.JobId;

import java.util.concurrent.TimeUnit;

public interface SchedulerService {

    void scheduleDailyTask(JobId id, Integer hour, Integer minute);

    void scheduleRepetitiveTask(JobId id, Long initialDelay, TimeUnit initialDelayTimeUnit, Long interval, TimeUnit timeUnit);

}
