package one.microproject.devicecontroller.dto;

public interface ClientAdapterWrapper<T> {

    T getClient();

    DeviceStatus getStatus();

    void setStatus(DeviceStatus deviceStatus);

    DeviceType getType();

}
