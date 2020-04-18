package itx.rpi.powercontroller.services.jobs;

import itx.rpi.powercontroller.config.Configuration;
import itx.rpi.powercontroller.config.JobConfiguration;
import itx.rpi.powercontroller.config.actions.ActionPortHighConfig;
import itx.rpi.powercontroller.config.actions.ActionPortLowConfig;
import itx.rpi.powercontroller.config.actions.ActionWaitConfig;
import itx.rpi.powercontroller.services.RPiService;
import itx.rpi.powercontroller.services.TaskManagerService;
import itx.rpi.powercontroller.services.jobs.impl.ActionPortHigh;
import itx.rpi.powercontroller.services.jobs.impl.ActionPortLow;
import itx.rpi.powercontroller.services.jobs.impl.ActionWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

public final class TaskManagerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(TaskManagerFactory.class);

    private TaskManagerFactory() {
    }

    public static TaskManagerService createTaskManagerService(Configuration configuration, RPiService rPiService) {
        LOG.info("creating task manager");
        Collection<Job> jobs = new ArrayList<>();
        Collection<JobConfiguration> jobConfigurations = configuration.getJobConfigurations();
        jobConfigurations.forEach(jc -> {
            LOG.info("creating jobId={}", jc.getId());
            Collection<Action> actions = new ArrayList<>();
            jc.getActions().forEach(c->{
                LOG.info("creating actionType={}", c.getType());
                if (ActionPortHighConfig.class.equals(c.getType())) {
                    ActionPortHighConfig config = (ActionPortHighConfig)c;
                    ActionPortHigh action = new ActionPortHigh(config.getPort(), rPiService);
                    actions.add(action);
                } else if (ActionPortLowConfig.class.equals(c.getType())) {
                    ActionPortLowConfig config = (ActionPortLowConfig)c;
                    ActionPortLow action = new ActionPortLow(config.getPort(), rPiService);
                    actions.add(action);
                } else if (ActionWaitConfig.class.equals(c.getType())) {
                    ActionWaitConfig config = (ActionWaitConfig)c;
                    ActionWait action = new ActionWait(config.getDelay(), config.getTimeUnitType());
                    actions.add(action);
                } else {
                    throw new UnsupportedOperationException("Unsupported Action Configuration Type: " + c.getType());
                }
            });
            Job job = new Job(jc.getId(), jc.getName(), actions);
            jobs.add(job);
        });
        return new TaskManagerServiceImpl(jobs);
    }

}
