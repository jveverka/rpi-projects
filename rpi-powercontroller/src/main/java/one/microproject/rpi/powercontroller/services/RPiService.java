package one.microproject.rpi.powercontroller.services;

import one.microproject.rpi.powercontroller.dto.Measurements;
import one.microproject.rpi.powercontroller.dto.SystemState;

import java.util.Optional;

public interface RPiService extends AutoCloseable {

    Measurements getMeasurements();

    SystemState getSystemState();

    Optional<Boolean> setPortState(Integer port, Boolean state);

}
