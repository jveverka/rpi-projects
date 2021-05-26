package one.microproject.devicecontroller.service;

import one.microproject.devicecontroller.dto.DeviceCreateRequest;
import one.microproject.devicecontroller.dto.DeviceInfo;
import one.microproject.devicecontroller.dto.DeviceType;
import one.microproject.devicecontroller.model.DeviceData;
import one.microproject.devicecontroller.repository.DeviceDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DeviceAdminServiceImpl implements DeviceAdminService {

    private final DeviceDataRepository repository;
    private final DataMapper dataMapper;

    public DeviceAdminServiceImpl(DeviceDataRepository repository, DataMapper dataMapper) {
        this.repository = repository;
        this.dataMapper = dataMapper;
    }

    @Override
    @Transactional
    public void addDevice(DeviceCreateRequest deviceCreateRequest) {
        repository.save(dataMapper.map(deviceCreateRequest));
    }

    @Override
    @Transactional
    public void removeDevice(String deviceId) {
        repository.deleteById(deviceId);
    }

    @Override
    public List<DeviceInfo> getAll() {
        List<DeviceInfo> deviceInfos = new ArrayList<>();
        repository.findAll().forEach(d -> deviceInfos.add(dataMapper.map(d)));
        return deviceInfos;
    }

    @Override
    public Optional<DeviceInfo> getById(String deviceId) {
        return repository.findById(deviceId).map(dataMapper::map);
    }

    @Override
    public Optional<DeviceData> getDeviceDataById(String deviceId) {
        return repository.findById(deviceId);
    }

    @Override
    public List<String> getSupportedDeviceTypes() {
        return List.of(
                DeviceType.DEVICE_SIM.getType(),
                DeviceType.RPI_CAMERA.getType(),
                DeviceType.RPI_POWER_CONTROLLER.getType()
        );
    }

}
