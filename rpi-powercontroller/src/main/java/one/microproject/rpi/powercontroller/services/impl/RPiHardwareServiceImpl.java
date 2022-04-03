package one.microproject.rpi.powercontroller.services.impl;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
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
    private final Map<Integer, GpioPinDigitalMultipurpose> ports;
    private final Map<Integer, PortType> portTypes;

    public RPiHardwareServiceImpl(PortListener portListener, Configuration configuration) {
        LOG.info("initializing hardware ...");
        Context context = Pi4J.newAutoContext();
        this.bmp180 = new BMP180(context);
        this.htu21DF = new HTU21DF(context);
        this.ports = new ConcurrentHashMap<>();
        this.portTypes = configuration.getPortsTypes();
        this.portTypes.forEach((k, v) -> {
            LOG.info("initializing port {} as {}", k, v);
            Pin pin = mapPin(k);
            GpioPinDigitalMultipurpose gpioPinDigitalMultipurpose = gpio.provisionDigitalMultipurposePin(pin, mapPinMode(v));
            gpioPinDigitalMultipurpose.addListener(new PinListener(k, portListener));
            if (PortType.OUTPUT.equals(v)) {
                gpioPinDigitalMultipurpose.setState(false);
            }
            if (PortType.INPUT.equals(v)) {

            }
            ports.put(k, gpioPinDigitalMultipurpose);
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
        ports.forEach((k,v) ->
            result.put(k, v.isHigh())
        );
        return new SystemState(new Date(), result, portTypes);
    }

    @Override
    public Optional<Boolean> setPortState(Integer port, Boolean state) {
        try {
            LOG.info("setting state port={}  state={}", port, state);
            GpioPinDigitalMultipurpose gpioPinDigitalMultipurpose = ports.get(port);
            gpioPinDigitalMultipurpose.setState(state);
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

    public static Pin mapPin(Integer port) {
        if (port == 0) {
            return RaspiPin.GPIO_00;
        } else if (port == 1) {
            return RaspiPin.GPIO_01;
        } else if (port == 2) {
            return RaspiPin.GPIO_02;
        } else if (port == 3) {
            return RaspiPin.GPIO_03;
        } else if (port == 4) {
            return RaspiPin.GPIO_04;
        } else if (port == 5) {
            return RaspiPin.GPIO_05;
        } else if (port == 6) {
            return RaspiPin.GPIO_06;
        } else if (port == 7) {
            return RaspiPin.GPIO_07;
        } else {
            throw new UnsupportedOperationException("Invalid Pin number " + port);
        }
    }

    public static PinMode mapPinMode(PortType portType) {
        if (PortType.INPUT.equals(portType)) {
            return PinMode.DIGITAL_INPUT;
        } else if (PortType.OUTPUT.equals(portType)) {
            return PinMode.DIGITAL_OUTPUT;
        } else {
            throw new UnsupportedOperationException("Invalid PortType number " + portType);
        }
    }

    private class PinListener implements GpioPinListenerDigital {

        private final Integer port;
        private final PortListener portListener;

        private PinListener(Integer port, PortListener portListener) {
            this.port = port;
            this.portListener = portListener;
        }

        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
            portListener.onStateChange(port, event.getState().isHigh());
        }

    }

}
