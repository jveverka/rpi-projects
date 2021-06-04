package one.microproject.rpi.powercontroller.services;

import one.microproject.rpi.device.dto.SystemInfo;
import one.microproject.rpi.powercontroller.dto.ControllerInfo;

public interface SystemInfoService {

    SystemInfo<ControllerInfo> getSystemInfo();

}
