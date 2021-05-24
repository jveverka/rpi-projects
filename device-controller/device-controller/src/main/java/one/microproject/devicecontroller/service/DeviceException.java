package one.microproject.devicecontroller.service;

public class DeviceException extends RuntimeException {

    public DeviceException(String message) {
        super(message);
    }

    public DeviceException(String message, Throwable t) {
        super(message, t);
    }

    public DeviceException(Throwable t) {
        super(t);
    }

}
