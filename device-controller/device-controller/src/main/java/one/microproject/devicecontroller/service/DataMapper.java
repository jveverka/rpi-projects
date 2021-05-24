package one.microproject.devicecontroller.service;

import one.microproject.devicecontroller.dto.DeviceCreateRequest;
import one.microproject.devicecontroller.dto.DeviceInfo;
import one.microproject.devicecontroller.model.DeviceData;
import org.springframework.stereotype.Component;

@Component
public class DataMapper {

    public DeviceData map(DeviceCreateRequest request)  {
        return new DeviceData(request.id(), request.type(), request.baseUrl(),
                request.credentials().clientId(), request.credentials().secret(), request.groupId());
    }

    public DeviceInfo map(DeviceData deviceData) {
        return new DeviceInfo(deviceData.getId(), deviceData.getType(), deviceData.getGroupId(), deviceData.getBaseUrl());
    }

}
