package itx.rpi.powercontroller.services.impl;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalMultipurpose;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.RaspiPin;
import itx.rpi.hardware.gpio.sensors.BMP180;
import itx.rpi.hardware.gpio.sensors.HTU21DF;
import itx.rpi.powercontroller.dto.Measurements;
import itx.rpi.powercontroller.dto.SystemState;
import itx.rpi.powercontroller.services.RPiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RPiHardwareServiceImpl implements RPiService {

    private static final Logger LOG = LoggerFactory.getLogger(RPiHardwareServiceImpl.class);

    private final BMP180 bmp180;
    private final HTU21DF htu21DF;
    private final GpioController gpio;
    private final Map<Integer, GpioPinDigitalMultipurpose> ports;

    public RPiHardwareServiceImpl() {
        LOG.info("initializing hardware ...");
        bmp180 = new BMP180();
        htu21DF = new HTU21DF();
        gpio = GpioFactory.getInstance();
        ports = new ConcurrentHashMap<>();
        GpioPinDigitalMultipurpose gpioPinDigitalMultipurpose = null;
        gpioPinDigitalMultipurpose = gpio.provisionDigitalMultipurposePin(RaspiPin.GPIO_00, "0", PinMode.DIGITAL_OUTPUT);
        ports.put(Integer.valueOf(0), gpioPinDigitalMultipurpose);
        gpioPinDigitalMultipurpose = gpio.provisionDigitalMultipurposePin(RaspiPin.GPIO_01, "1", PinMode.DIGITAL_OUTPUT);
        ports.put(Integer.valueOf(1), gpioPinDigitalMultipurpose);
        gpioPinDigitalMultipurpose = gpio.provisionDigitalMultipurposePin(RaspiPin.GPIO_02, "2", PinMode.DIGITAL_OUTPUT);
        ports.put(Integer.valueOf(2), gpioPinDigitalMultipurpose);
        gpioPinDigitalMultipurpose = gpio.provisionDigitalMultipurposePin(RaspiPin.GPIO_03, "3", PinMode.DIGITAL_OUTPUT);
        ports.put(Integer.valueOf(3), gpioPinDigitalMultipurpose);
        gpioPinDigitalMultipurpose = gpio.provisionDigitalMultipurposePin(RaspiPin.GPIO_04, "4", PinMode.DIGITAL_OUTPUT);
        ports.put(Integer.valueOf(4), gpioPinDigitalMultipurpose);
        gpioPinDigitalMultipurpose = gpio.provisionDigitalMultipurposePin(RaspiPin.GPIO_05, "5", PinMode.DIGITAL_OUTPUT);
        ports.put(Integer.valueOf(5), gpioPinDigitalMultipurpose);
        gpioPinDigitalMultipurpose = gpio.provisionDigitalMultipurposePin(RaspiPin.GPIO_06, "6", PinMode.DIGITAL_OUTPUT);
        ports.put(Integer.valueOf(6), gpioPinDigitalMultipurpose);
        gpioPinDigitalMultipurpose = gpio.provisionDigitalMultipurposePin(RaspiPin.GPIO_07, "7", PinMode.DIGITAL_OUTPUT);
        ports.put(Integer.valueOf(7), gpioPinDigitalMultipurpose);
        LOG.info("hardware initialization done.");
    }

    @Override
    public Measurements getMeasurements() {
        try {
            float temperature = bmp180.readTemperature();
            float pressure = bmp180.readPressure();
            float relHumidity = htu21DF.readHumidity();
            return new Measurements(temperature, "celsius", relHumidity, "percent", pressure, "kPa");
        } catch (Exception e) {
            return new Measurements(null, "celsius", null, "percent", null, "kPa");
        }
    }

    @Override
    public SystemState getSystemState() {
        Map<Integer, Boolean> result = new HashMap<>();
        ports.forEach((k,v) -> {
            result.put(k, v.isHigh());
        });
        return new SystemState(result);
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
        gpio.shutdown();
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

}
