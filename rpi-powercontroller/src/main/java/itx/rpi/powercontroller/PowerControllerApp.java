package itx.rpi.powercontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.PathHandler;
import itx.rpi.powercontroller.config.Configuration;
import itx.rpi.powercontroller.dto.JobId;
import itx.rpi.powercontroller.handlers.CancelAllTasksHandler;
import itx.rpi.powercontroller.handlers.CancelTaskHandler;
import itx.rpi.powercontroller.handlers.JobInfoHandler;
import itx.rpi.powercontroller.handlers.JobKillAllIdHandler;
import itx.rpi.powercontroller.handlers.MeasurementsHandler;
import itx.rpi.powercontroller.handlers.PortStateHandler;
import itx.rpi.powercontroller.handlers.SubmitTaskHandler;
import itx.rpi.powercontroller.handlers.SystemInfoHandler;
import itx.rpi.powercontroller.handlers.SystemStateHandler;
import itx.rpi.powercontroller.handlers.TasksInfoHandler;
import itx.rpi.powercontroller.services.AAService;
import itx.rpi.powercontroller.services.PortListener;
import itx.rpi.powercontroller.services.RPiService;
import itx.rpi.powercontroller.services.ShutdownHook;
import itx.rpi.powercontroller.services.SystemInfoService;
import itx.rpi.powercontroller.services.TaskManagerService;
import itx.rpi.powercontroller.services.impl.AAServiceImpl;
import itx.rpi.powercontroller.services.impl.PortListenerImpl;
import itx.rpi.powercontroller.services.impl.RPiServiceFactory;
import itx.rpi.powercontroller.services.impl.SystemInfoServiceImpl;
import itx.rpi.powercontroller.services.jobs.TaskManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PowerControllerApp {

    private static final Logger LOG = LoggerFactory.getLogger(PowerControllerApp.class);

    public static Services initialize(ObjectMapper mapper, Configuration configuration) {
        AAService aaService = new AAServiceImpl(configuration.getCredentials());
        PortListener portListener = new PortListenerImpl(configuration.getKeyEvents());
        SystemInfoService systemInfoService = new SystemInfoServiceImpl(configuration);
        RPiService rPiService = RPiServiceFactory.createService(configuration, portListener);
        TaskManagerService taskManagerService = TaskManagerFactory.createTaskManagerService(configuration, rPiService);
        portListener.setTaskManagerService(taskManagerService);

        configuration.getExecuteJobsOnStart().forEach(jobId -> {
            LOG.info("Starting \"jobId={}\" job on start.", jobId);
            taskManagerService.submitTask(JobId.from(jobId));
        });

        PathHandler handler = Handlers.path()
                .addPrefixPath("/system/info", new SystemInfoHandler(mapper, aaService, systemInfoService))
                .addPrefixPath("/system/measurements", new MeasurementsHandler(mapper, aaService, rPiService))
                .addPrefixPath("/system/state", new SystemStateHandler(mapper, aaService, rPiService))
                .addPrefixPath("/system/port", new BlockingHandler(new PortStateHandler(mapper, aaService, rPiService)))
                .addPrefixPath("/system/jobs", new JobInfoHandler(mapper, aaService, taskManagerService))
                .addPrefixPath("/system/jobs/killalljobid", new JobKillAllIdHandler(mapper, aaService, taskManagerService))
                .addPrefixPath("/system/tasks", new TasksInfoHandler(mapper, aaService, taskManagerService))
                .addPrefixPath("/system/tasks/submit", new BlockingHandler(new SubmitTaskHandler(mapper, aaService, taskManagerService)))
                .addPrefixPath("/system/tasks/cancel", new BlockingHandler(new CancelTaskHandler(mapper, aaService, taskManagerService)))
                .addPrefixPath("/system/tasks/cancel/all", new BlockingHandler(new CancelAllTasksHandler(mapper, aaService, taskManagerService)));


        Undertow server = Undertow.builder()
                .addHttpListener(configuration.getPort(), configuration.getHost())
                .setHandler(handler)
                .build();

        return new Services(rPiService, server, taskManagerService, portListener);
    }

    public static void main(String[] args) throws IOException {
        LOG.info("Starting PowerController APP ...");
        long timeStart = System.currentTimeMillis();
        ObjectMapper mapper = new ObjectMapper();
        Configuration configuration = null;
        if (args.length > 0) {
            try {
                LOG.info("Loading configuration from file: {}", args[0]);
                configuration = mapper.readValue(new File(args[0]), Configuration.class);
            } catch (IOException e) {
                LOG.error("ERROR: Configuration loading from file failed ! {}", args[0]);
                return;
            }
        } else {
            LOG.info("Using default configuration.");
            InputStream is = PowerControllerApp.class.getResourceAsStream("/configuration.json");
            configuration = mapper.readValue(is, Configuration.class);
        }
        LOG.info("#CONFIG: id={}", configuration.getId());
        LOG.info("#CONFIG: name={}", configuration.getName());
        LOG.info("#CONFIG: host={}", configuration.getHost());
        LOG.info("#CONFIG: port={}", configuration.getPort());
        LOG.info("#CONFIG: hardware={}", configuration.isHardware());

        LOG.info("initializing services ...");
        Services services = initialize(mapper, configuration);

        LOG.info("registering shutdown hook ...");
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(services));
        LOG.info("Server started in {} ms", (System.currentTimeMillis() - timeStart));
        services.getServer().start();
    }

    public static class Services {

        private final RPiService rPiService;
        private final Undertow server;
        private final TaskManagerService taskManagerService;
        private final PortListener portListener;

        public Services(RPiService rPiService, Undertow server, TaskManagerService taskManagerService, PortListener portListener) {
            this.rPiService = rPiService;
            this.server = server;
            this.taskManagerService = taskManagerService;
            this.portListener = portListener;
        }

        public RPiService getRPiService() {
            return rPiService;
        }

        public Undertow getServer() {
            return server;
        }

        public TaskManagerService getTaskManagerService() {
            return taskManagerService;
        }

        public PortListener getPortListener() {
            return portListener;
        }

        public void shutdown() throws Exception {
            this.server.stop();
            this.taskManagerService.close();
            this.rPiService.close();
        }

    }

}
