package itx.rpi.powercontroller.services.impl;

import itx.rpi.powercontroller.dto.Measurements;
import itx.rpi.powercontroller.dto.SystemState;
import itx.rpi.powercontroller.services.RPiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RPiSimulatedServiceImpl implements RPiService {

    private static final Logger LOG = LoggerFactory.getLogger(RPiSimulatedServiceImpl.class);

    private final Map<Integer, Boolean> ports;

    public RPiSimulatedServiceImpl() {
        ports = new ConcurrentHashMap<>();
        for (int i=0; i<8; i++) {
            ports.put(i, false);
        }
    }

    @Override
    public Measurements getMeasurements() {
        return new Measurements(null, "celsius", null, "percent", null, "kPa");
    }

    @Override
    public SystemState getSystemState() {
        return new SystemState(ports);
    }

    @Override
    public Optional<Boolean> setPortState(Integer port, Boolean state) {
        if (ports.containsKey(port)) {
            ports.put(port, state);
            return Optional.of(state);
        }
        return Optional.empty();
    }

    @Override
    public void close() throws Exception {
        LOG.info("closing");
    }

}
