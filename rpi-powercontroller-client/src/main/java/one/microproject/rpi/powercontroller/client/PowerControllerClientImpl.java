package one.microproject.rpi.powercontroller.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import one.microproject.rpi.device.dto.SystemInfo;
import one.microproject.rpi.powercontroller.ClientException;
import one.microproject.rpi.powercontroller.PowerControllerClient;
import one.microproject.rpi.powercontroller.dto.JobId;
import one.microproject.rpi.powercontroller.dto.JobInfo;
import one.microproject.rpi.powercontroller.dto.Measurements;
import one.microproject.rpi.powercontroller.dto.SetPortRequest;
import one.microproject.rpi.powercontroller.dto.ControllerInfo;
import one.microproject.rpi.powercontroller.dto.SystemState;
import one.microproject.rpi.powercontroller.dto.TaskFilter;
import one.microproject.rpi.powercontroller.dto.TaskId;
import one.microproject.rpi.powercontroller.dto.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.Collection;
import java.util.Optional;

public class PowerControllerClientImpl implements PowerControllerClient {

    private static final Logger LOG = LoggerFactory.getLogger(PowerControllerClientImpl.class);
    private static final String AUTHORIZATION  = "Authorization";
    private static final String APPLICATION_JSON = "application/json";
    private static final String ERROR_MESSAGE = "Expected http=200, received http=";

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    private final URL baseURL;
    private final String clientId;
    private final String clientSecret;

    public PowerControllerClientImpl(URL baseURL, String clientId, String clientSecret, OkHttpClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
        this.baseURL = baseURL;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public SystemInfo<ControllerInfo> getSystemInfo() {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/system/info")
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(clientId, clientSecret))
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), new TypeReference<SystemInfo<ControllerInfo>>(){});
            }
            throw new ClientException(ERROR_MESSAGE + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public SystemState getSystemState() {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/system/state")
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(clientId, clientSecret))
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), SystemState.class);
            }
            throw new ClientException(ERROR_MESSAGE + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public Measurements getMeasurements() {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/system/measurements")
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(clientId, clientSecret))
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), Measurements.class);
            }
            throw new ClientException(ERROR_MESSAGE + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public Collection<JobInfo> getSystemJobs() {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/system/jobs")
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(clientId, clientSecret))
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), new TypeReference<Collection<JobInfo>>(){});
            }
            throw new ClientException(ERROR_MESSAGE + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public JobId getKillAllJobId() {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/system/jobs/killalljobid")
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(clientId, clientSecret))
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), JobId.class);
            }
            throw new ClientException(ERROR_MESSAGE + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public Collection<TaskInfo> getAllTasks() {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/system/tasks")
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(clientId, clientSecret))
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), new TypeReference<Collection<TaskInfo>>(){});
            }
            throw new ClientException(ERROR_MESSAGE + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public Collection<TaskInfo> getTasks(TaskFilter filter) {
        try {
            String requestBody = mapper.writeValueAsString(filter);
            Request request = new Request.Builder()
                    .url(baseURL + "/system/tasks")
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(clientId, clientSecret))
                    .put(RequestBody.create(requestBody, MediaType.parse(APPLICATION_JSON)))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), new TypeReference<Collection<TaskInfo>>(){});
            }
            throw new ClientException(ERROR_MESSAGE + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public boolean setPortState(Integer port, Boolean state) {
        try {
            SetPortRequest setPortRequest = new SetPortRequest(port, state);
            String requestBody = mapper.writeValueAsString(setPortRequest);
            Request request = new Request.Builder()
                    .url(baseURL + "/system/port")
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(clientId, clientSecret))
                    .put(RequestBody.create(requestBody, MediaType.parse(APPLICATION_JSON)))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return 200 == response.code();
            }
            throw new ClientException(ERROR_MESSAGE + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public Optional<TaskId> submitTask(JobId id) {
        try {
            String requestBody = mapper.writeValueAsString(id);
            Request request = new Request.Builder()
                    .url(baseURL + "/system/tasks/submit")
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(clientId, clientSecret))
                    .put(RequestBody.create(requestBody, MediaType.parse(APPLICATION_JSON)))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return Optional.of(mapper.readValue(response.body().string(), TaskId.class));
            }
            LOG.info(ERROR_MESSAGE, response.code());
            return Optional.empty();
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public boolean cancelTask(TaskId id) {
        try {
            String requestBody = mapper.writeValueAsString(id);
            Request request = new Request.Builder()
                    .url(baseURL + "/system/tasks/cancel")
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(clientId, clientSecret))
                    .put(RequestBody.create(requestBody, MediaType.parse(APPLICATION_JSON)))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return 200 == response.code();
            }
            throw new ClientException(ERROR_MESSAGE + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public boolean cancelAllTasks() {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/system/tasks/cancel/all")
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(clientId, clientSecret))
                    .put(RequestBody.create("{}", MediaType.parse(APPLICATION_JSON)))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return 200 == response.code();
            }
            throw new ClientException(ERROR_MESSAGE + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public boolean waitForTaskStarted(TaskId id) {
        try {
            String requestBody = mapper.writeValueAsString(id);
            Request request = new Request.Builder()
                    .url(baseURL + "/system/tasks/wait/started")
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(clientId, clientSecret))
                    .put(RequestBody.create(requestBody, MediaType.parse(APPLICATION_JSON)))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return 200 == response.code();
            }
            throw new ClientException(ERROR_MESSAGE + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public boolean waitForTaskTermination(TaskId id) {
        try {
            String requestBody = mapper.writeValueAsString(id);
            Request request = new Request.Builder()
                    .url(baseURL + "/system/tasks/wait/termination")
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(clientId, clientSecret))
                    .put(RequestBody.create(requestBody, MediaType.parse(APPLICATION_JSON)))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return 200 == response.code();
            }
            throw new ClientException(ERROR_MESSAGE + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public boolean cleanTaskQueue() {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/system/tasks/clean")
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(clientId, clientSecret))
                    .put(RequestBody.create("{}", MediaType.parse(APPLICATION_JSON)))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return 200 == response.code();
            }
            throw new ClientException(ERROR_MESSAGE + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    public static String createBasicAuthorizationFromCredentials(String clientId, String clientSecret) {
        String authorization = clientId + ":" + clientSecret;
        byte[] encodedBytes = Base64.getEncoder().encode(authorization.getBytes());
        String encodedString = new String(encodedBytes);
        return "Basic " + encodedString;
    }

}
