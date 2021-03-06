package one.microproject.devicecontroller.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import one.microproject.devicecontroller.dto.ArrayWrapper;
import one.microproject.devicecontroller.dto.ClientAdapterWrapper;
import one.microproject.devicecontroller.dto.DataStream;
import one.microproject.devicecontroller.dto.DeviceQuery;
import one.microproject.devicecontroller.dto.DeviceQueryResponse;
import one.microproject.devicecontroller.dto.DeviceStatus;
import one.microproject.devicecontroller.dto.DeviceType;
import one.microproject.devicecontroller.dto.ResponseStatus;
import one.microproject.devicecontroller.dto.ResultId;
import one.microproject.devicecontroller.model.DeviceData;
import one.microproject.rpi.camera.client.CameraClient;
import one.microproject.rpi.camera.client.dto.CameraConfiguration;
import one.microproject.rpi.camera.client.dto.CameraInfo;
import one.microproject.rpi.camera.client.dto.CameraSelect;
import one.microproject.rpi.camera.client.dto.ImageCapture;
import one.microproject.rpi.camera.client.dto.Resolutions;
import one.microproject.rpi.camera.client.dto.Rotations;
import one.microproject.rpi.device.dto.SystemInfo;
import one.microproject.rpi.device.sim.DeviceSim;
import one.microproject.rpi.device.sim.dto.DataRequest;
import one.microproject.rpi.device.sim.dto.DataResponse;
import one.microproject.rpi.device.sim.dto.SimInfo;
import one.microproject.rpi.powercontroller.PowerControllerClient;
import one.microproject.rpi.powercontroller.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Optional;

@Service
public class DeviceDataServiceImpl implements DeviceDataService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceDataServiceImpl.class);

    private final ClientAdapterFactory clientAdapterFactory;
    private final DeviceAdminService deviceAdminService;
    private final ObjectMapper objectMapper;


    public DeviceDataServiceImpl(ClientAdapterFactory clientAdapterFactory, DeviceAdminService deviceAdminService, ObjectMapper objectMapper) {
        this.clientAdapterFactory = clientAdapterFactory;
        this.deviceAdminService = deviceAdminService;
        this.objectMapper = objectMapper;
    }

    @Override
    public DeviceQueryResponse query(DeviceQuery query) throws DeviceException {
        try {
            Optional<DeviceData> deviceInfo = deviceAdminService.getDataById(query.deviceId());
            if (deviceInfo.isPresent()) {
                ClientAdapterWrapper<?> clientAdapterWrapper = clientAdapterFactory.createOrGet(deviceInfo.get());
                return getDeviceResponse(clientAdapterWrapper, query);
            } else {
                LOG.error("Device id={} Not Found !", query.deviceId());
                throw new DeviceException("Device id=" + query.deviceId() + " Not Found !");
            }
        } catch (Exception e) {
            throw new DeviceException("Device query exception.", e);
        }
    }

    @Override
    public DataStream download(DeviceQuery query) throws DeviceException {
        try {
            Optional<DeviceData> deviceInfo = deviceAdminService.getDataById(query.deviceId());
            if (deviceInfo.isPresent()) {
                ClientAdapterWrapper<?> clientAdapterWrapper = clientAdapterFactory.createOrGet(deviceInfo.get());
                return getDeviceData(clientAdapterWrapper, query);
            } else {
                LOG.error("Device id={} Not Found !", query.deviceId());
                throw new DeviceException("Device id=" + query.deviceId() + " Not Found !");
            }
        } catch (Exception e) {
            throw new DeviceException("Device query exception.", e);
        }
    }

    private DeviceQueryResponse getDeviceResponse(ClientAdapterWrapper<?> clientAdapterWrapper, DeviceQuery query) throws JsonProcessingException {
        DeviceQueryResponse response = new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), ResponseStatus.ERROR, null);
        if (DeviceType.DEVICE_SIM.equals(clientAdapterWrapper.getType())) {
            DeviceSim deviceSim = (DeviceSim) clientAdapterWrapper.getClient();
            try {
                if ("system-info".equals(query.queryType())) {
                    SystemInfo<SimInfo> systemInfo = deviceSim.getSystemInfo();
                    ObjectNode objectNode = objectMapper.valueToTree(systemInfo);
                    response = new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), ResponseStatus.OK, objectNode);
                } else if ("data".equals(query.queryType())) {
                    DataRequest dataRequest = objectMapper.treeToValue(query.payload(), DataRequest.class);
                    DataResponse dataResponse = deviceSim.getData(dataRequest);
                    ObjectNode objectNode = objectMapper.valueToTree(dataResponse);
                    response = new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), ResponseStatus.OK, objectNode);
                } else {
                    throw new UnsupportedOperationException("Unsupported query type=" + query.queryType() + "  for device type=" + clientAdapterWrapper.getType());
                }
                clientAdapterWrapper.setStatus(DeviceStatus.ONLINE);
            } catch (Exception e) {
                clientAdapterWrapper.setStatus(DeviceStatus.OFFLINE);
            }
            return response;
        } else if (DeviceType.RPI_POWER_CONTROLLER.equals(clientAdapterWrapper.getType())) {
            PowerControllerClient powerControllerClient = (PowerControllerClient) clientAdapterWrapper.getClient();
            try {
                if ("system-info".equals(query.queryType())) {
                    SystemInfo<ControllerInfo> systemInfo = powerControllerClient.getSystemInfo();
                    ObjectNode objectNode = objectMapper.valueToTree(systemInfo);
                    response = new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), ResponseStatus.OK, objectNode);
                } else if ("measurements".equals(query.queryType())) {
                    Measurements measurements = powerControllerClient.getMeasurements();
                    ObjectNode objectNode = objectMapper.valueToTree(measurements);
                    response = new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), ResponseStatus.OK, objectNode);
                } else if ("state".equals(query.queryType())) {
                    SystemState systemState = powerControllerClient.getSystemState();
                    ObjectNode objectNode = objectMapper.valueToTree(systemState);
                    response = new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), ResponseStatus.OK, objectNode);
                } else if ("jobs".equals(query.queryType())) {
                    ArrayWrapper wrapper = new ArrayWrapper(powerControllerClient.getSystemJobs());
                    ObjectNode objectNode = objectMapper.valueToTree(wrapper);
                    response = new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), ResponseStatus.OK, objectNode);
                } else if ("tasks".equals(query.queryType())) {
                    if (query.payload() !=  null) {
                        //get filtered tasks
                        TaskFilter filter = objectMapper.treeToValue(query.payload(), TaskFilter.class);
                        ArrayWrapper wrapper = new ArrayWrapper(powerControllerClient.getTasks(filter));
                        ObjectNode objectNode = objectMapper.valueToTree(wrapper);
                        response = new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), ResponseStatus.OK, objectNode);
                    } else {
                        //get all tasks
                        ArrayWrapper wrapper = new ArrayWrapper(powerControllerClient.getAllTasks());
                        ObjectNode objectNode = objectMapper.valueToTree(wrapper);
                        response = new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), ResponseStatus.OK, objectNode);
                    }
                } else if ("submit-task".equals(query.queryType())) {
                    JobId jobId = objectMapper.treeToValue(query.payload(), JobId.class);
                    Optional<TaskId> taskId = powerControllerClient.submitTask(jobId);
                    ResultId resultId = taskId.map(r -> new ResultId(r.getId(), true)).orElse(new ResultId(null, false));
                    ObjectNode objectNode = objectMapper.valueToTree(resultId);
                    response = new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), ResponseStatus.OK, objectNode);
                } else if ("cancel-task".equals(query.queryType())) {
                    TaskId taskId = objectMapper.treeToValue(query.payload(), TaskId.class);
                    boolean result = powerControllerClient.cancelTask(taskId);
                    ResultId resultId = new ResultId(taskId.getId(), result);
                    ObjectNode objectNode = objectMapper.valueToTree(resultId);
                    response = new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), ResponseStatus.OK, objectNode);
                } else {
                    throw new UnsupportedOperationException("Unsupported query type=" + query.queryType() + "  for device type=" + clientAdapterWrapper.getType());
                }
                clientAdapterWrapper.setStatus(DeviceStatus.ONLINE);
            } catch (Exception e) {
                clientAdapterWrapper.setStatus(DeviceStatus.OFFLINE);
            }
            return response;
        } else if (DeviceType.RPI_CAMERA.equals(clientAdapterWrapper.getType())) {
            try {
                CameraClient cameraClient = (CameraClient) clientAdapterWrapper.getClient();
                if ("system-info".equals(query.queryType())) {
                    SystemInfo<CameraInfo> systemInfo = cameraClient.getSystemInfo();
                    ObjectNode objectNode = objectMapper.valueToTree(systemInfo);
                    response = new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), ResponseStatus.OK, objectNode);
                } else if ("set-config".equals(query.queryType())) {
                    CameraConfiguration requestedConfiguration = objectMapper.treeToValue(query.payload(), CameraConfiguration.class);
                    CameraConfiguration effectiveConfiguration = cameraClient.setConfiguration(requestedConfiguration);
                    ObjectNode objectNode = objectMapper.valueToTree(effectiveConfiguration);
                    response = new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), ResponseStatus.OK, objectNode);
                } else if ("get-config".equals(query.queryType())) {
                    CameraConfiguration effectiveConfiguration = cameraClient.getConfiguration();
                    ObjectNode objectNode = objectMapper.valueToTree(effectiveConfiguration);
                    response = new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), ResponseStatus.OK, objectNode);
                } else if ("get-resolutions".equals(query.queryType())) {
                    Resolutions resolutions = cameraClient.getResolutions();
                    ObjectNode objectNode = objectMapper.valueToTree(resolutions);
                    response = new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), ResponseStatus.OK, objectNode);
                } else if ("get-rotations".equals(query.queryType())) {
                    Rotations rotations = cameraClient.getRotations();
                    ObjectNode objectNode = objectMapper.valueToTree(rotations);
                    response = new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), ResponseStatus.OK, objectNode);
                } else if ("get-selected-camera".equals(query.queryType())) {
                    CameraSelect selectedCamera = cameraClient.getSelectedCamera();
                    ObjectNode objectNode = objectMapper.valueToTree(selectedCamera);
                    response = new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), ResponseStatus.OK, objectNode);
                } else if ("select-camera".equals(query.queryType())) {
                    CameraSelect requestedSelectedCamera = objectMapper.treeToValue(query.payload(), CameraSelect.class);
                    CameraSelect selectedCamera = cameraClient.selectCamera(requestedSelectedCamera);
                    ObjectNode objectNode = objectMapper.valueToTree(selectedCamera);
                    response = new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), ResponseStatus.OK, objectNode);
                } else {
                    throw new UnsupportedOperationException("Unsupported query type=" + query.queryType() + "  for device type=" + clientAdapterWrapper.getType());
                }
                clientAdapterWrapper.setStatus(DeviceStatus.ONLINE);
            } catch (Exception e) {
                clientAdapterWrapper.setStatus(DeviceStatus.OFFLINE);
            }
            return response;
        } else {
            throw new UnsupportedOperationException("Unsupported device type=" + clientAdapterWrapper.getType());
        }
    }

    private DataStream getDeviceData(ClientAdapterWrapper<?> clientAdapterWrapper, DeviceQuery query) throws JsonProcessingException {
        if (DeviceType.DEVICE_SIM.equals(clientAdapterWrapper.getType())) {
            DeviceSim deviceSim = (DeviceSim) clientAdapterWrapper.getClient();
            if ("download".equals(query.queryType())) {
                DataRequest dataRequest = objectMapper.treeToValue(query.payload(), DataRequest.class);
                InputStream is = deviceSim.download(dataRequest);
                clientAdapterWrapper.setStatus(DeviceStatus.ONLINE);
                return new DataStream(is, "data.file", "text/plain");
            } else {
                clientAdapterWrapper.setStatus(DeviceStatus.OFFLINE);
                throw new UnsupportedOperationException("Unsupported query type=" + query.queryType() + "  for device type=" + clientAdapterWrapper.getType());
            }
        } else if (DeviceType.RPI_CAMERA.equals(clientAdapterWrapper.getType())) {
            CameraClient cameraClient = (CameraClient) clientAdapterWrapper.getClient();
            if ("capture".equals(query.queryType())) {
                 ImageCapture imageCapture = cameraClient.captureImage();
                 clientAdapterWrapper.setStatus(DeviceStatus.ONLINE);
                 return new DataStream(imageCapture.getIs(), imageCapture.getFileName(), imageCapture.getMimeType());
            } else {
                clientAdapterWrapper.setStatus(DeviceStatus.OFFLINE);
                throw new UnsupportedOperationException("Unsupported query type=" + query.queryType() + "  for device type=" + clientAdapterWrapper.getType());
            }
        } else {
            throw new UnsupportedOperationException("Unsupported device type=" + clientAdapterWrapper.getType());
        }
    }

}