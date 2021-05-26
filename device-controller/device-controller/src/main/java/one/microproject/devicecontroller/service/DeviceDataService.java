package one.microproject.devicecontroller.service;

import one.microproject.devicecontroller.dto.DataStream;
import one.microproject.devicecontroller.dto.DeviceQuery;
import one.microproject.devicecontroller.dto.DeviceQueryResponse;

public interface DeviceDataService {

    DeviceQueryResponse query(DeviceQuery query) throws DeviceException;

    DataStream download(DeviceQuery query) throws DeviceException;

}
