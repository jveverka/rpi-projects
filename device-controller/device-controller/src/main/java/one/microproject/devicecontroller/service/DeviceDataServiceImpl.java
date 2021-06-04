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
import one.microproject.rpi.camera.client.CameraClient;
import one.microproject.rpi.camera.client.dto.CameraInfo;
import one.microproject.rpi.camera.client.dto.ImageCapture;
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

import java.util.Optional;

@Service
public class DeviceDataServiceImpl implements DeviceDataService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceDataServiceImpl.class);

    private final ClientAdapterFactory clientAdapterFactory;
    private final ObjectMapper objectMapper;


    public DeviceDataServiceImpl(ClientAdapterFactory clientAdapterFactory, ObjectMapper objectMapper) {
        this.clientAdapterFactory = clientAdapterFactory;
        this.objectMapper = objectMapper;
    }

    @Override
    public DeviceQueryResponse query(DeviceQuery query) throws DeviceException {
        try {
            Optional<ClientAdapterWrapper<?>> clientAdapterOptional = clientAdapterFactory.get(query.deviceId());
            if (clientAdapterOptional.isPresent()) {
                ClientAdapterWrapper<?> clientAdapterWrapper = clientAdapterOptional.get();
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
            Optional<ClientAdapterWrapper<?>> clientAdapterOptional = clientAdapterFactory.get(query.deviceId());
            if (clientAdapterOptional.isPresent()) {
                ClientAdapterWrapper<?> clientAdapterWrapper = clientAdapterOptional.get();
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
                    ArrayWrapper wrapper = new ArrayWrapper(powerControllerClient.getAllTasks());
                    ObjectNode objectNode = objectMapper.valueToTree(wrapper);
                    response = new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), ResponseStatus.OK, objectNode);
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
                return new DataStream(deviceSim.download(dataRequest), "data.file", "text/plain");
            } else {
                throw new UnsupportedOperationException("Unsupported query type=" + query.queryType() + "  for device type=" + clientAdapterWrapper.getType());
            }
        } else if (DeviceType.RPI_CAMERA.equals(clientAdapterWrapper.getType())) {
            CameraClient cameraClient = (CameraClient) clientAdapterWrapper.getClient();
            if ("capture".equals(query.queryType())) {
                ImageCapture imageCapture = cameraClient.captureImage();
                return new DataStream(imageCapture.getIs(), imageCapture.getFileName(), imageCapture.getMimeType());
            } else {
                throw new UnsupportedOperationException("Unsupported query type=" + query.queryType() + "  for device type=" + clientAdapterWrapper.getType());
            }
        } else {
            throw new UnsupportedOperationException("Unsupported device type=" + clientAdapterWrapper.getType());
        }
    }

}