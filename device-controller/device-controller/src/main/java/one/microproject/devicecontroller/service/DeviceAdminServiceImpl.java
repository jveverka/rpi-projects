package one.microproject.devicecontroller.service;

import one.microproject.devicecontroller.dto.ClientAdapterWrapper;
import one.microproject.devicecontroller.dto.DeviceCreateRequest;
import one.microproject.devicecontroller.dto.DeviceInfo;
import one.microproject.devicecontroller.dto.DeviceStatus;
import one.microproject.devicecontroller.dto.DeviceType;
import one.microproject.devicecontroller.model.DeviceData;
import one.microproject.devicecontroller.repository.DeviceDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DeviceAdminServiceImpl implements DeviceAdminService {

    private final DeviceDataRepository repository;
    private final DataMapper dataMapper;
    private final ClientAdapterFactory clientAdapterFactory;

    public DeviceAdminServiceImpl(DeviceDataRepository repository, DataMapper dataMapper, ClientAdapterFactory clientAdapterFactory) {
        this.repository = repository;
        this.dataMapper = dataMapper;
        this.clientAdapterFactory = clientAdapterFactory;
    }

    @Override
    @Transactional
    public void addDevice(DeviceCreateRequest deviceCreateRequest) throws DeviceException {
        try {
            Optional<DeviceData> deviceDataOptional = repository.findById(deviceCreateRequest.id());
            if (deviceDataOptional.isEmpty()) {
                DeviceData deviceData = dataMapper.map(deviceCreateRequest);
                ClientAdapterWrapper<?> clientAdapterWrapper = clientAdapterFactory.create(deviceData);
                clientAdapterWrapper.checkStatus();
                repository.save(deviceData);
            } else {
                throw new DeviceException("Device id=" + deviceCreateRequest.id() + " already exists !");
            }
        } catch (MalformedURLException e) {
            throw new DeviceException(e);
        }
    }

    @Override
    @Transactional
    public void removeDevice(String deviceId) {
        repository.deleteById(deviceId);
        clientAdapterFactory.remove(deviceId);
    }

    @Override
    public List<DeviceInfo> getAll() {
        List<DeviceInfo> deviceInfos = new ArrayList<>();
        repository.findAll().forEach(d -> {
            Optional<ClientAdapterWrapper<?>> clientAdapterWrapper = clientAdapterFactory.get(d.getId());
            if (clientAdapterWrapper.isPresent()) {
                deviceInfos.add(dataMapper.map(d, clientAdapterWrapper.get().getStatus()));
            } else {
                deviceInfos.add(dataMapper.map(d, DeviceStatus.OFFLINE));
            }
        });
        return deviceInfos;
    }

    @Override
    public Optional<DeviceInfo> getById(String deviceId) {
        return repository.findById(deviceId).map(d -> {
            Optional<ClientAdapterWrapper<?>> clientAdapterWrapper = clientAdapterFactory.get(d.getId());
            if (clientAdapterWrapper.isPresent()) {
                return dataMapper.map(d, clientAdapterWrapper.get().getStatus());
            } else {
                return dataMapper.map(d, DeviceStatus.OFFLINE);
            }
        });
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
