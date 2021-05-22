package one.microproject.rpi.camera.client.dto;

public enum ImageFormat {

    JPEG("jpeg"),
    PNG("png");

    private final String format;

    ImageFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

}
