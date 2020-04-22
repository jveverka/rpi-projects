package itx.rpi.powercontroller.services.jobs;

import itx.rpi.powercontroller.config.Configuration;
import itx.rpi.powercontroller.config.JobConfiguration;
import itx.rpi.powercontroller.services.RPiService;
import itx.rpi.powercontroller.services.TaskManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

public final class TaskManagerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(TaskManagerFactory.class);

    private TaskManagerFactory() {
    }

    public static TaskManagerService createTaskManagerService(Configuration configuration, Job killAllTasksJob, RPiService rPiService) {
        LOG.info("Creating task manager");
        Collection<Job> jobs = new ArrayList<>();
        Collection<JobConfiguration> jobConfigurations = configuration.getJobConfigurations();
        jobConfigurations.forEach(jc -> {
            LOG.info("creating jobId={}", jc.getId());
            Job job = new Job(jc.getId(), jc.getName(), jc.getActions());
            jobs.add(job);
        });
        return new TaskManagerServiceImpl(jobs, killAllTasksJob, rPiService);
    }

}
