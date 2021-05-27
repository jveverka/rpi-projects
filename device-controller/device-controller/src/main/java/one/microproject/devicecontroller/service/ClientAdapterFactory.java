package one.microproject.devicecontroller.service;

import one.microproject.devicecontroller.dto.ClientAdapterWrapper;
import one.microproject.devicecontroller.model.DeviceData;

import java.net.MalformedURLException;

public interface ClientAdapterFactory {

    ClientAdapterWrapper<?> get(DeviceData deviceData) throws MalformedURLException;

}
