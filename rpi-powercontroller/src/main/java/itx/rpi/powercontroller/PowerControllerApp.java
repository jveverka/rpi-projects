package itx.rpi.powercontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.PathHandler;
import itx.rpi.powercontroller.config.Configuration;
import itx.rpi.powercontroller.handlers.MeasurementsHandler;
import itx.rpi.powercontroller.handlers.PortStateHandler;
import itx.rpi.powercontroller.handlers.SystemInfoHandler;
import itx.rpi.powercontroller.handlers.SystemStateHandler;
import itx.rpi.powercontroller.services.PortListener;
import itx.rpi.powercontroller.services.RPiService;
import itx.rpi.powercontroller.services.SystemInfoService;
import itx.rpi.powercontroller.services.impl.PortListenerImpl;
import itx.rpi.powercontroller.services.impl.RPiServiceFactory;
import itx.rpi.powercontroller.services.impl.SystemInfoServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PowerControllerApp {

    private static final Logger LOG = LoggerFactory.getLogger(PowerControllerApp.class);

    public static void main(String[] args) throws IOException {
        LOG.info("Starting PowerController APP ...");
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

        PortListener portListener = new PortListenerImpl();
        SystemInfoService systemInfoService = new SystemInfoServiceImpl(configuration);
        RPiService rPiService = RPiServiceFactory.createService(configuration, portListener);

        PathHandler handler = Handlers.path()
                .addPrefixPath("/system/info", new SystemInfoHandler(mapper, systemInfoService))
                .addPrefixPath("/system/measurements", new MeasurementsHandler(mapper, rPiService))
                .addPrefixPath("/system/state", new SystemStateHandler(mapper, rPiService))
                .addPrefixPath("/system/port", new BlockingHandler(new PortStateHandler(mapper, rPiService)));

        Undertow server = Undertow.builder()
                .addHttpListener(configuration.getPort(), configuration.getHost())
                .setHandler(handler)
                .build();
        server.start();
    }

}
