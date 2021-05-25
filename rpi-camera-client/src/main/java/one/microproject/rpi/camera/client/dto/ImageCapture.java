package one.microproject.rpi.camera.client.dto;

import java.io.InputStream;

public class ImageCapture {

    private final InputStream is;
    private final String fileName;
    private final String mimeType;

    public ImageCapture(InputStream is, String fileName, String mimeType) {
        this.is = is;
        this.fileName = fileName;
        this.mimeType = mimeType;
    }

    public InputStream getIs() {
        return is;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

}
