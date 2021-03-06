package one.microproject.devicecontroller.service;

import okhttp3.OkHttpClient;
import one.microproject.devicecontroller.dto.ClientAdapterWrapper;
import one.microproject.devicecontroller.dto.DeviceType;
import one.microproject.devicecontroller.model.DeviceData;
import one.microproject.devicecontroller.service.devices.ClientAdapterCamera;
import one.microproject.devicecontroller.service.devices.ClientAdapterPowerController;
import one.microproject.devicecontroller.service.devices.ClientAdapterSim;
import one.microproject.rpi.camera.client.CameraClient;
import one.microproject.rpi.camera.client.CameraClientBuilder;
import one.microproject.rpi.device.sim.DeviceSim;
import one.microproject.rpi.device.sim.DeviceSimBuilder;
import one.microproject.rpi.powercontroller.PowerControllerClient;
import one.microproject.rpi.powercontroller.PowerControllerClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClientAdapterFactoryImpl implements ClientAdapterFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ClientAdapterFactoryImpl.class);

    private final OkHttpClient okHttpClient;
    private final Map<String, ClientAdapterWrapper<?>> clients;

    public ClientAdapterFactoryImpl(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        this.clients = new ConcurrentHashMap<>();
    }

    @Override
    public ClientAdapterWrapper<?> createOrGet(DeviceData deviceData) throws MalformedURLException {
        if (!clients.containsKey(deviceData.getId())) {
            LOG.debug("initializing client deviceId={} type={}", deviceData.getId(), deviceData.getType());
            if (DeviceType.DEVICE_SIM.getType().equals(deviceData.getType())) {
                DeviceSim deviceSim = DeviceSimBuilder.builder()
                        .withDeviceId(deviceData.getId())
                        .build();
                ClientAdapterSim clientAdapterWrapper = new ClientAdapterSim(deviceSim);
                clientAdapterWrapper.checkStatus();
                clients.put(deviceData.getId(), clientAdapterWrapper);
            } else if (DeviceType.RPI_POWER_CONTROLLER.getType().equals(deviceData.getType())) {
                PowerControllerClient powerControllerClient = PowerControllerClientBuilder.builder()
                        .baseUrl(deviceData.getBaseUrl())
                        .withCredentials(deviceData.getClientId(), deviceData.getClientSecret())
                        .setHttpClient(okHttpClient)
                        .build();
                ClientAdapterPowerController clientAdapterWrapper = new ClientAdapterPowerController(powerControllerClient);
                clientAdapterWrapper.checkStatus();
                clients.put(deviceData.getId(), clientAdapterWrapper);
            } else if (DeviceType.RPI_CAMERA.getType().equals(deviceData.getType())) {
                CameraClient cameraClient = CameraClientBuilder.builder()
                        .baseUrl(deviceData.getBaseUrl())
                        .withCredentials(deviceData.getClientId(), deviceData.getClientSecret())
                        .setHttpClient(okHttpClient)
                        .build();
                ClientAdapterCamera clientAdapterWrapper = new ClientAdapterCamera(cameraClient);
                clientAdapterWrapper.checkStatus();
                clients.put(deviceData.getId(), clientAdapterWrapper);
            } else {
                throw new UnsupportedOperationException("Unknown or unsupported device type " + deviceData.getType());
            }
        } else {
            LOG.debug("reusing client deviceId={} type={}", deviceData.getId(), deviceData.getType());
        }
        return clients.get(deviceData.getId());
    }

}
