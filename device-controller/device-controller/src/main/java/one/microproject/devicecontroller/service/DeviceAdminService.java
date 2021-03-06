package one.microproject.devicecontroller.service;

import one.microproject.devicecontroller.dto.DeviceCreateRequest;
import one.microproject.devicecontroller.dto.DeviceInfo;
import one.microproject.devicecontroller.model.DeviceData;

import java.util.List;
import java.util.Optional;

public interface DeviceAdminService {

    void addDevice(DeviceCreateRequest deviceCreateRequest) throws DeviceException;

    void removeDevice(String deviceId);

    List<DeviceInfo> getAll();

    Optional<DeviceInfo> getById(String deviceId);

    Optional<DeviceData> getDataById(String deviceId);

    List<String> getSupportedDeviceTypes();

}
