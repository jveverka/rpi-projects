package one.microproject.devicecontroller.dto;

public class ClientAdapterWrapper<T> {

    private final T client;

    public ClientAdapterWrapper(T client) {
        this.client = client;
    }

    public T getClient() {
        return client;
    }

}
