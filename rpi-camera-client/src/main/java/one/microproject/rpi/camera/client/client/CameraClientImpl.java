package one.microproject.rpi.camera.client.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import one.microproject.rpi.camera.client.CameraClient;
import one.microproject.rpi.camera.client.ClientException;
import one.microproject.rpi.camera.client.dto.ImageCapture;
import one.microproject.rpi.camera.client.dto.ImageFormat;
import one.microproject.rpi.camera.client.dto.Resolution;
import one.microproject.rpi.camera.client.dto.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
    public SystemInfo getSystemInfo() {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/system/info")
                    .addHeader(AUTHORIZATION, createBasicAuthorizationFromCredentials(clientId, clientSecret))
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), SystemInfo.class);
            }
            LOG.warn("Http error: {}", response.code());
            throw new ClientException(ERROR_MESSAGE + response.code());
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public ImageCapture captureImage() {
        return captureImageAction(null, null, null);
    }

    @Override
    public ImageCapture captureImage(Float shutterSpeed) {
        return captureImageAction(shutterSpeed, null, null);
    }

    @Override
    public ImageCapture captureImage(Float shutterSpeed, ImageFormat imageFormat) {
        return captureImageAction(shutterSpeed, imageFormat, null);
    }

    @Override
    public ImageCapture captureImage(Float shutterSpeed, ImageFormat imageFormat, Resolution resolution) {
        return captureImageAction(shutterSpeed, imageFormat, resolution);
    }

    @Override
    public ImageCapture captureImage(Float shutterSpeed, Resolution resolution) {
        return captureImageAction(shutterSpeed, null, resolution);
    }

    public static String createBasicAuthorizationFromCredentials(String clientId, String clientSecret) {
        String authorization = clientId + ":" + clientSecret;
        byte[] encodedBytes = Base64.getEncoder().encode(authorization.getBytes());
        String encodedString = new String(encodedBytes);
        return "Basic " + encodedString;
    }

    private ImageCapture captureImageAction(Float shutterSpeed, ImageFormat imageFormat, Resolution resolution) {
        try {
            HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(baseURL + "/system/capture").newBuilder();
            if (shutterSpeed != null) {
                httpUrlBuilder.addQueryParameter("shutter-speed", shutterSpeed.toString());
            }
            if (imageFormat != null) {
                httpUrlBuilder.addQueryParameter("format", imageFormat.getFormat());
            }
            if (resolution != null) {
                httpUrlBuilder.addQueryParameter("resolution", resolution.getResolution());
            }
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

}
