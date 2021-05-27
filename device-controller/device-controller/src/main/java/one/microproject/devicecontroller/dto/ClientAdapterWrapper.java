package one.microproject.devicecontroller.dto;

public interface ClientAdapterWrapper<T> {

    T getClient();

    DeviceStatus getStatus();

    DeviceStatus checkStatus();

    void setStatus(DeviceStatus deviceStatus);

    DeviceType getType();

}
