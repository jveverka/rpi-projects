package one.microproject.rpi.camera.client.dto;

public enum Resolution {

    M1("1M"),
    M2("2M"),
    M5("5M"),
    M8("8M");

    private final String resolution;

    Resolution(String resolution) {
        this.resolution = resolution;
    }

    public String getResolution() {
        return resolution;
    }

}
