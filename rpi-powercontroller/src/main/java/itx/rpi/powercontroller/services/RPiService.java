package itx.rpi.powercontroller.services;

import itx.rpi.powercontroller.dto.Measurements;
import itx.rpi.powercontroller.dto.SystemState;

import java.util.Optional;

public interface RPiService extends AutoCloseable {

    Measurements getMeasurements();

    SystemState getSystemState();

    Optional<Boolean> setPortState(Integer port, Boolean state);

}
