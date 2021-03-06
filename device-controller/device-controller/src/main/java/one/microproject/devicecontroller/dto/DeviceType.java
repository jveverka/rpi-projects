package one.microproject.devicecontroller.dto;

public enum DeviceType {

    DEVICE_SIM("device-sim"),
    RPI_CAMERA("rpi-camera"),
    RPI_POWER_CONTROLLER("rpi-power-controller");

    private final String type;

    DeviceType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
