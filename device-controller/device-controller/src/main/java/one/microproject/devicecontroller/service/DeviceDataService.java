package one.microproject.devicecontroller.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import one.microproject.devicecontroller.dto.DeviceQuery;
import one.microproject.devicecontroller.dto.DeviceQueryResponse;

import java.io.InputStream;

public interface DeviceDataService {

    DeviceQueryResponse query(DeviceQuery query) throws DeviceException;

    InputStream download(DeviceQuery query) throws DeviceException;

}
