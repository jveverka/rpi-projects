package one.microproject.devicecontroller.service;

import one.microproject.devicecontroller.dto.ClientAdapterWrapper;
import one.microproject.devicecontroller.model.DeviceData;

import java.net.MalformedURLException;
import java.util.Optional;

public interface ClientAdapterFactory {

    ClientAdapterWrapper<?> create(DeviceData deviceData) throws MalformedURLException;

    Optional<ClientAdapterWrapper<?>> get(String id);

    void remove(String id);

}
