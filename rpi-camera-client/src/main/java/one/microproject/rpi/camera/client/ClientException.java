package one.microproject.rpi.camera.client;

public class ClientException extends RuntimeException {

    public ClientException(String message) {
        super(message);
    }

    public ClientException(Throwable t) {
        super(t);
    }

}
