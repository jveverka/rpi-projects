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
    private final Map<Integer, PortType> portTypes;

    public RPiHardwareServiceImpl(PortListener portListener, Configuration configuration) {
        LOG.info("initializing hardware ...");
        Context context = Pi4J.newAutoContext();
        this.bmp180 = new BMP180(context);
        this.htu21DF = new HTU21DF(context);
        this.outPorts = new ConcurrentHashMap<>();
        this.inPorts = new ConcurrentHashMap<>();
        this.portTypes = configuration.getPortsTypes();
        this.portTypes.forEach((k, v) -> {
            int pinId = mapPin(k);
            LOG.info("initializing port {} pinId={} as {}", k, pinId, v);
            if (PortType.OUTPUT.equals(v)) {
                DigitalOutput digitalOutput = context.dout().create(pinId);
                digitalOutput.low();
                outPorts.put(k, digitalOutput);
            }
            if (PortType.INPUT.equals(v)) {
                DigitalInput digitalInput = context.din().create(pinId);
                digitalInput.addListener(new PinListener(k, portListener));
                inPorts.put(k, digitalInput);
            }
        });
        LOG.info("hardware initialization done.");
    }

    @Override
    public Measurements getMeasurements() {
        try {
            float temperature = bmp180.readTemperature();
            float pressure = bmp180.readPressure() / 1000f;
            float relHumidity = htu21DF.readHumidity();
            return new Measurements(new Date(), temperature, "celsius", relHumidity, "percent", pressure, "kPa");
        } catch (Exception e) {
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
        return new SystemState(new Date(), result, portTypes);
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

    public static Integer mapPin(Integer port) {
        if (port == 0) {
            return 0;
        } else if (port == 1) {
            return 1;
        } else if (port == 2) {
            return 2;
        } else if (port == 3) {
            return 3;
        } else if (port == 4) {
            return 4;
        } else if (port == 5) {
            return 5;
        } else if (port == 6) {
            return 6;
        } else if (port == 7) {
            return 7;
        } else {
            throw new UnsupportedOperationException("Invalid Pin number " + port);
        }
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
            portListener.onStateChange(port, event.state().isHigh());
        }

    }

}
