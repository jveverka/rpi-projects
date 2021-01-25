package one.microproject.rpi.powercontroller.services.impl;

import one.microproject.rpi.powercontroller.config.Configuration;
import one.microproject.rpi.powercontroller.dto.PortType;
import one.microproject.rpi.powercontroller.dto.Measurements;
import one.microproject.rpi.powercontroller.dto.SystemState;
import one.microproject.rpi.powercontroller.services.PortListener;
import one.microproject.rpi.powercontroller.services.RPiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RPiSimulatedServiceImpl implements RPiService {

    private static final Logger LOG = LoggerFactory.getLogger(RPiSimulatedServiceImpl.class);

    private final Map<Integer, Boolean> ports;
    private final Map<Integer, PortType> portTypes;
    private final PortListener portListener;

    public RPiSimulatedServiceImpl(PortListener portListener, Configuration configuration) {
        this.portListener = portListener;
        this.portTypes = configuration.getPortsTypes();
        this.ports = new ConcurrentHashMap<>();
        this.portTypes.forEach((k,v) -> this.ports.put(k, false));
    }

    @Override
    public Measurements getMeasurements() {
        return new Measurements(new Date(),null, "celsius", null, "percent", null, "kPa");
    }

    @Override
    public SystemState getSystemState() {
        return new SystemState(new Date(), ports, portTypes);
    }

    @Override
    public Optional<Boolean> setPortState(Integer port, Boolean state) {
        if (ports.containsKey(port) && PortType.OUTPUT.equals(portTypes.get(port))) {
            ports.put(port, state);
            portListener.onStateChange(port, state);
            return Optional.of(state);
        }
        return Optional.empty();
    }

    @Override
    public void close() throws Exception {
        LOG.info("closing");
    }

}
