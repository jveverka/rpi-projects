package one.microproject.rpi.powercontroller.services.impl;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalStateChangeEvent;
import com.pi4j.io.gpio.digital.DigitalStateChangeListener;
import one.microproject.rpi.hardware.gpio.sensors.sensors.BMP180;
import one.microproject.rpi.hardware.gpio.sensors.sensors.HTU21DF;
import one.microproject.rpi.powercontroller.config.Configuration;
import one.microproject.rpi.powercontroller.dto.PortMapping;
import one.microproject.rpi.powercontroller.dto.PortType;
import one.microproject.rpi.powercontroller.dto.Measurements;
import one.microproject.rpi.powercontroller.dto.SystemState;
import one.microproject.rpi.powercontroller.services.PortListener;
import one.microproject.rpi.powercontroller.services.RPiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RPiHardwareServiceImpl implements RPiService {

    private static final Logger LOG = LoggerFactory.getLogger(RPiHardwareServiceImpl.class);

    private final BMP180 bmp180;
    private final HTU21DF htu21DF;
    private final Map<Integer, DigitalOutput> outPorts;
    private final Map<Integer, DigitalInput> inPorts;
    private final Map<Integer, PortMapping> portMapping;

    public RPiHardwareServiceImpl(PortListener portListener, Configuration configuration) {
        LOG.info("Initializing RPi hardware ...");
        Context context = Pi4J.newAutoContext();
        this.bmp180 = new BMP180(context);
        this.htu21DF = new HTU21DF(context);
        this.outPorts = new ConcurrentHashMap<>();
        this.inPorts = new ConcurrentHashMap<>();
        this.portMapping = configuration.getPortsMapping();
        this.portMapping.forEach((k, v) -> {
            int pinId = v.getAddress();
            LOG.info("Initializing port {} pinId={} as {}", k, pinId, v);
            if (PortType.OUTPUT.equals(v.getType())) {
                DigitalOutput digitalOutput = context.dout().create(pinId);
                digitalOutput.low();
                outPorts.put(k, digitalOutput);
                LOG.info("OUTPUT port created");
            }
            if (PortType.INPUT.equals(v.getType())) {
                DigitalInput digitalInput = context.din().create(pinId);
                digitalInput.addListener(new PinListener(k, portListener));
                inPorts.put(k, digitalInput);
                LOG.info("INPUT port created");
            }
        });
        LOG.info("INPUT  {}", inPorts.size());
        LOG.info("OUTPUT {}", outPorts.size());
        LOG.info("Hardware initialization done.");
    }

    @Override
    public Measurements getMeasurements() {
        try {
            LOG.debug("getMeasurements");
            float temperature = bmp180.readTemperature();
            float pressure = bmp180.readPressure() / 1000f;
            float relHumidity = htu21DF.readHumidity();
            return new Measurements(new Date(), temperature, "celsius", relHumidity, "percent", pressure, "kPa");
        } catch (Exception e) {
            LOG.error("ERROR: ", e);
            return new Measurements(new Date(),null, "celsius", null, "percent", null, "kPa");
        }
    }

    @Override
    public SystemState getSystemState() {
        Map<Integer, Boolean> result = new HashMap<>();
        outPorts.forEach((k,v) -> {
            result.put(k, v.isHigh());
        });
        inPorts.forEach((k,v) ->
            result.put(k, v.isHigh())
        );
        return new SystemState(new Date(), result, portMapping);
    }

    @Override
    public Optional<Boolean> setPortState(Integer port, Boolean state) {
        try {
            LOG.info("setting state port={}  state={}", port, state);
            DigitalOutput digitalOutput = outPorts.get(port);
            digitalOutput.setState(state);
            return Optional.of(state);
        } catch (Exception e) {
            LOG.error("Exception: ", e);
            return Optional.empty();
        }
    }

    @Override
    public void close() throws Exception {
        LOG.info("closing");
        bmp180.close();
        htu21DF.close();
    }

    private class PinListener implements DigitalStateChangeListener {

        private final Integer port;
        private final PortListener portListener;

        private PinListener(Integer port, PortListener portListener) {
            this.port = port;
            this.portListener = portListener;
        }

        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            LOG.info("onDigitalStateChange: {} {}", event.state().getName(), event.state().getValue());
            portListener.onStateChange(port, event.state().isHigh());
        }

    }

}
