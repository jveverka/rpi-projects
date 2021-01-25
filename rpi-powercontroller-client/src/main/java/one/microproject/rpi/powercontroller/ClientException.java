package one.microproject.rpi.powercontroller;

public class ClientException extends RuntimeException {

    public ClientException(String message) {
        super(message);
    }

    public ClientException(Throwable t) {
        super(t);
    }

}
