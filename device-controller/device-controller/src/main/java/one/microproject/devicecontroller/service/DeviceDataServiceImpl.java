package one.microproject.devicecontroller.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.OkHttpClient;
import one.microproject.clientsim.ClientSim;
import one.microproject.clientsim.dto.DataRequest;
import one.microproject.clientsim.dto.DataResponse;
import one.microproject.clientsim.dto.SystemInfo;
import one.microproject.devicecontroller.dto.JobsWrapper;
import one.microproject.devicecontroller.dto.ClientAdapterWrapper;
import one.microproject.devicecontroller.dto.DataStream;
import one.microproject.devicecontroller.dto.DeviceQuery;
import one.microproject.devicecontroller.dto.DeviceQueryResponse;
import one.microproject.devicecontroller.dto.DeviceType;
import one.microproject.devicecontroller.dto.ResultId;
import one.microproject.devicecontroller.dto.TasksWrapper;
import one.microproject.devicecontroller.model.DeviceData;
import one.microproject.rpi.camera.client.CameraClient;
import one.microproject.rpi.camera.client.dto.ImageCapture;
import one.microproject.rpi.powercontroller.PowerControllerClient;
import one.microproject.rpi.powercontroller.dto.JobId;
import one.microproject.rpi.powercontroller.dto.Measurements;
import one.microproject.rpi.powercontroller.dto.SystemState;
import one.microproject.rpi.powercontroller.dto.TaskId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeviceDataServiceImpl implements DeviceDataService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceDataServiceImpl.class);

    private final DeviceAdminService deviceAdminService;
    private final ClientAdapterFactory clientAdapterFactory;
    private final ObjectMapper objectMapper;


    public DeviceDataServiceImpl(DeviceAdminService deviceAdminService, ClientAdapterFactory clientAdapterFactory, ObjectMapper objectMapper) {
        this.deviceAdminService = deviceAdminService;
        this.clientAdapterFactory = clientAdapterFactory;
        this.objectMapper = objectMapper;
    }

    @Override
    public DeviceQueryResponse query(DeviceQuery query) throws DeviceException {
        try {
            Optional<DeviceData> deviceDataOptional = deviceAdminService.getDeviceDataById(query.deviceId());
            if (deviceDataOptional.isPresent()) {
                ClientAdapterWrapper<?> clientAdapterWrapper = clientAdapterFactory.get(deviceDataOptional.get());
                return getDeviceResponse(clientAdapterWrapper, deviceDataOptional.get(), query);
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
            Optional<DeviceData> deviceDataOptional = deviceAdminService.getDeviceDataById(query.deviceId());
            if (deviceDataOptional.isPresent()) {
                ClientAdapterWrapper<?> clientAdapterWrapper = clientAdapterFactory.get(deviceDataOptional.get());
                return getDeviceData(clientAdapterWrapper, deviceDataOptional.get(), query);
            } else {
                LOG.error("Device id={} Not Found !", query.deviceId());
                throw new DeviceException("Device id=" + query.deviceId() + " Not Found !");
            }
        } catch (Exception e) {
            throw new DeviceException("Device query exception.", e);
        }
    }

    private DeviceQueryResponse getDeviceResponse(ClientAdapterWrapper<?> clientAdapterWrapper, DeviceData deviceData, DeviceQuery query) throws JsonProcessingException {
        if (DeviceType.DEVICE_SIM.getType().equals(deviceData.getType())) {
            ClientSim clientSim = (ClientSim) clientAdapterWrapper.getClient();
            if ("system-info".equals(query.queryType())) {
                SystemInfo systemInfo = clientSim.getSystemInfo();
                ObjectNode objectNode = objectMapper.valueToTree(systemInfo);
                return new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), objectNode);
            } else if ("data".equals(query.queryType())) {
                DataRequest dataRequest = objectMapper.treeToValue(query.payload(), DataRequest.class);
                DataResponse dataResponse = clientSim.getData(dataRequest);
                ObjectNode objectNode = objectMapper.valueToTree(dataResponse);
                return new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), objectNode);
            } else {
                throw new UnsupportedOperationException("Unsupported query type=" + query.queryType() + "  for device type=" + deviceData.getType());
            }
        } else if (DeviceType.RPI_POWER_CONTROLLER.getType().equals(deviceData.getType())) {
            PowerControllerClient powerControllerClient = (PowerControllerClient) clientAdapterWrapper.getClient();
            if ("system-info".equals(query.queryType())) {
                one.microproject.rpi.powercontroller.dto.SystemInfo systemInfo = powerControllerClient.getSystemInfo();
                ObjectNode objectNode = objectMapper.valueToTree(systemInfo);
                return new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), objectNode);
            } else if ("measurements".equals(query.queryType())) {
                Measurements measurements = powerControllerClient.getMeasurements();
                ObjectNode objectNode = objectMapper.valueToTree(measurements);
                return new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), objectNode);
            } else if ("state".equals(query.queryType())) {
                SystemState systemState = powerControllerClient.getSystemState();
                ObjectNode objectNode = objectMapper.valueToTree(systemState);
                return new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), objectNode);
            } else if ("jobs".equals(query.queryType())) {
                JobsWrapper wrapper = new JobsWrapper(powerControllerClient.getSystemJobs());
                ObjectNode objectNode = objectMapper.valueToTree(wrapper);
                return new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), objectNode);
            } else if ("tasks".equals(query.queryType())) {
                TasksWrapper wrapper = new TasksWrapper(powerControllerClient.getAllTasks());
                ObjectNode objectNode = objectMapper.valueToTree(wrapper);
                return new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), objectNode);
            } else if ("submit-task".equals(query.queryType())) {
                JobId jobId = objectMapper.treeToValue(query.payload(), JobId.class);
                Optional<TaskId> taskId = powerControllerClient.submitTask(jobId);
                ResultId resultId = taskId.map(r -> new ResultId(r.getId(), true)).orElse(new ResultId(null, false));
                ObjectNode objectNode = objectMapper.valueToTree(resultId);
                return new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), objectNode);
            } else if ("cancel-task".equals(query.queryType())) {
                TaskId taskId = objectMapper.treeToValue(query.payload(), TaskId.class);
                boolean result = powerControllerClient.cancelTask(taskId);
                ResultId resultId = new ResultId(taskId.getId(), result);
                ObjectNode objectNode = objectMapper.valueToTree(resultId);
                return new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), objectNode);
            } else {
                throw new UnsupportedOperationException("Unsupported query type=" + query.queryType() + "  for device type=" + deviceData.getType());
            }
        } else if (DeviceType.RPI_CAMERA.getType().equals(deviceData.getType())) {
            CameraClient cameraClient = (CameraClient) clientAdapterWrapper.getClient();
            if ("system-info".equals(query.queryType())) {
                one.microproject.rpi.camera.client.dto.SystemInfo systemInfo = cameraClient.getSystemInfo();
                ObjectNode objectNode = objectMapper.valueToTree(systemInfo);
                return new DeviceQueryResponse(query.id(), query.deviceId(), query.queryType(), objectNode);
            } else {
                throw new UnsupportedOperationException("Unsupported query type=" + query.queryType() + "  for device type=" + deviceData.getType());
            }
        } else {
            throw new UnsupportedOperationException("Unsupported device type=" + deviceData.getType());
        }
    }

    private DataStream getDeviceData(ClientAdapterWrapper<?> clientAdapterWrapper, DeviceData deviceData, DeviceQuery query) throws JsonProcessingException {
        if (DeviceType.DEVICE_SIM.getType().equals(deviceData.getType())) {
            ClientSim clientSim = (ClientSim) clientAdapterWrapper.getClient();
            if ("download".equals(query.queryType())) {
                DataRequest dataRequest = objectMapper.treeToValue(query.payload(), DataRequest.class);
                return new DataStream(clientSim.download(dataRequest), "data.file", "text/plain");
            } else {
                throw new UnsupportedOperationException("Unsupported query type=" + query.queryType() + "  for device type=" + deviceData.getType());
            }
        } else if (DeviceType.RPI_CAMERA.getType().equals(deviceData.getType())) {
            CameraClient cameraClient = (CameraClient) clientAdapterWrapper.getClient();
            if ("capture".equals(query.queryType())) {
                ImageCapture imageCapture = cameraClient.captureImage();
                return new DataStream(imageCapture.getIs(), imageCapture.getFileName(), imageCapture.getMimeType());
            } else {
                throw new UnsupportedOperationException("Unsupported query type=" + query.queryType() + "  for device type=" + deviceData.getType());
            }
        } else {
            throw new UnsupportedOperationException("Unsupported device type=" + deviceData.getType());
        }
    }

}