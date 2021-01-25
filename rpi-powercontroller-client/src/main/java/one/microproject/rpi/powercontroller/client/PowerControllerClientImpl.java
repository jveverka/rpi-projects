package one.microproject.rpi.powercontroller.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import one.microproject.rpi.powercontroller.ClientException;
import one.microproject.rpi.powercontroller.PowerControllerClient;
import one.microproject.rpi.powercontroller.dto.JobId;
import one.microproject.rpi.powercontroller.dto.JobInfo;
import one.microproject.rpi.powercontroller.dto.Measurements;
import one.microproject.rpi.powercontroller.dto.SetPortRequest;
import one.microproject.rpi.powercontroller.dto.SystemInfo;
import one.microproject.rpi.powercontroller.dto.SystemState;
import one.microproject.rpi.powercontroller.dto.TaskId;
import one.microproject.rpi.powercontroller.dto.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;
import java.util.Collection;
import java.util.Optional;

public class PowerControllerClientImpl implements PowerControllerClient {

    private static final Logger LOG = LoggerFactory.getLogger(PowerControllerClientImpl.class);
    private static final String AUTHORIZATION  = "Authorization";

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    private final String baseURL;
    private final String userName;
    private final String password;

    public PowerControllerClientImpl(String baseURL, String userName, String password) {
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();
        this.baseURL = baseURL;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public SystemInfo getSystemInfo() {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/system/info")
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(userName, password))
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), SystemInfo.class);
            }
            throw new ClientException("Expected http=200, received http=" + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public SystemState getSystemState() {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/system/state")
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(userName, password))
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), SystemState.class);
            }
            throw new ClientException("Expected http=200, received http=" + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public Measurements getMeasurements() {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/system/measurements")
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(userName, password))
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), Measurements.class);
            }
            throw new ClientException("Expected http=200, received http=" + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public Collection<JobInfo> getSystemJobs() {
        return null;
    }

    @Override
    public void killAllJobs() {

    }

    @Override
    public Collection<TaskInfo> getAllTasks() {
        return null;
    }

    @Override
    public boolean setPortState(SetPortRequest request) {
        return false;
    }

    @Override
    public Optional<TaskId> submitTask(JobId id) {
        return Optional.empty();
    }

    @Override
    public boolean cancelTask(TaskId id) {
        return false;
    }

    @Override
    public boolean cancelAllTasks() {
        return false;
    }

    @Override
    public boolean waitForTaskStarted(TaskId id) {
        return false;
    }

    @Override
    public boolean waitForTaskTermination(TaskId id) {
        return false;
    }

    @Override
    public boolean cleanTaskQueue() {
        return false;
    }

    public static String createBasicAuthorizationFromCredentials(String clientId, String clientSecret) {
        String authorization = clientId + ":" + clientSecret;
        byte[] encodedBytes = Base64.getEncoder().encode(authorization.getBytes());
        String encodedString = new String(encodedBytes);
        return "Basic " + encodedString;
    }

}
