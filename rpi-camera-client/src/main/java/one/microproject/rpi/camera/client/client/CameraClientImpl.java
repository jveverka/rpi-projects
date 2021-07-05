package one.microproject.rpi.camera.client.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import one.microproject.rpi.camera.client.CameraClient;
import one.microproject.rpi.camera.client.ClientException;
import one.microproject.rpi.camera.client.dto.CameraConfiguration;
import one.microproject.rpi.camera.client.dto.CameraSelect;
import one.microproject.rpi.camera.client.dto.ImageCapture;
import one.microproject.rpi.camera.client.dto.CameraInfo;
import one.microproject.rpi.camera.client.dto.Resolutions;
import one.microproject.rpi.camera.client.dto.Rotations;
import one.microproject.rpi.device.dto.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

public class CameraClientImpl implements CameraClient {

    private static final Logger LOG = LoggerFactory.getLogger(CameraClientImpl.class);
    private static final String AUTHORIZATION  = "Authorization";
    private static final String ERROR_MESSAGE = "Expected http=200, received http=";

    private final URL baseURL;
    private final String clientId;
    private final String clientSecret;
    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public CameraClientImpl(URL baseURL, String clientId, String clientSecret, OkHttpClient client, ObjectMapper mapper) {
        this.baseURL = baseURL;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public SystemInfo<CameraInfo> getSystemInfo() {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/system/info")
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(clientId, clientSecret))
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), new TypeReference<SystemInfo<CameraInfo>>(){});
            }
            LOG.warn("Http error: {}", response.code());
            throw new ClientException(ERROR_MESSAGE + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public CameraConfiguration getConfiguration() {
        return getData("/system/config", CameraConfiguration.class);
    }

    @Override
    public CameraConfiguration setConfiguration(CameraConfiguration configuration) {
        try {
            RequestBody body = RequestBody.create(mapper.writeValueAsString(configuration), MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(baseURL + "/system/config")
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(clientId, clientSecret))
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), CameraConfiguration.class);
            }
            LOG.warn("Http error: {}", response.code());
            throw new ClientException(ERROR_MESSAGE + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public ImageCapture captureImage() {
        try {
            HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(baseURL + "/system/capture").newBuilder();
            Request request = new Request.Builder()
                    .url(httpUrlBuilder.build())
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(clientId, clientSecret))
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                String mimeType = response.header("Content-Type");
                String fileName = getFileNameFromHeader(response.header("Content-Disposition"), mimeType);
                LOG.debug("Http OK: {} {}", mimeType, fileName);
                return new ImageCapture(response.body().byteStream(), fileName, mimeType);
            }
            LOG.warn("Http error: {}", response.code());
            throw new ClientException(ERROR_MESSAGE + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public InputStream captureVideo() {
        try {
            HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(baseURL + "/system/stream.mjpg").newBuilder();
            Request request = new Request.Builder()
                    .url(httpUrlBuilder.build())
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(clientId, clientSecret))
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                String mimeType = response.header("Content-Type");
                String fileName = getFileNameFromHeader(response.header("Content-Disposition"), mimeType);
                LOG.debug("Http OK: {} {}", mimeType, fileName);
                return response.body().byteStream();
            }
            LOG.warn("Http error: {}", response.code());
            throw new ClientException(ERROR_MESSAGE + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public CameraSelect selectCamera(CameraSelect cameraSelectRequest) {
        try {
            RequestBody body = RequestBody.create(mapper.writeValueAsString(cameraSelectRequest), MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(baseURL + "/system/camera")
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(clientId, clientSecret))
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), CameraSelect.class);
            }
            LOG.warn("Http error: {}", response.code());
            throw new ClientException(ERROR_MESSAGE + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public CameraSelect getSelectedCamera() {
        return getData("/system/camera", CameraSelect.class);
    }

    @Override
    public Resolutions getResolutions() {
        return getData("/system/resolutions", Resolutions.class);
    }

    @Override
    public Rotations getRotations() {
        return getData("/system/rotations", Rotations.class);
    }

    public static String createBasicAuthorizationFromCredentials(String clientId, String clientSecret) {
        String authorization = clientId + ":" + clientSecret;
        byte[] encodedBytes = Base64.getEncoder().encode(authorization.getBytes());
        String encodedString = new String(encodedBytes);
        return "Basic " + encodedString;
    }

    public static String getFileNameFromHeader(String contentDisposition, String mimeType) {
        try {
            try {
                String[] split = contentDisposition.split(";");
                return split[1].split("=")[1].trim();
            } catch (Exception e) {
                return "capture-image." + mimeType.split("/")[1];
            }
        } catch (Exception e) {
            return "capture-image.jpg";
        }
    }

    private <T> T getData(String path,  Class<T> type) {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + path)
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(clientId, clientSecret))
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), type);
            }
            LOG.warn("Http error: {}", response.code());
            throw new ClientException(ERROR_MESSAGE + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

}
