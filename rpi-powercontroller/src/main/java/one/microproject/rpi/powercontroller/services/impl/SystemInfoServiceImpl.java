package one.microproject.rpi.powercontroller.services.impl;

import one.microproject.rpi.device.dto.SystemInfo;
import one.microproject.rpi.powercontroller.config.Configuration;
import one.microproject.rpi.powercontroller.dto.ControllerInfo;
import one.microproject.rpi.powercontroller.services.SystemInfoService;

import java.util.Date;

public class SystemInfoServiceImpl implements SystemInfoService {

    private final Configuration configuration;

    public SystemInfoServiceImpl(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SystemInfo<ControllerInfo> getSystemInfo() {
        long timeStamp = new Date().getTime();
        long uptime = timeStamp - configuration.getStarted().getTime();
        ControllerInfo info = new ControllerInfo(configuration.isHardware(), configuration.getStarted(), ServiceUtils.calculateUptimeDays(uptime));
        return new SystemInfo<>(configuration.getId(), "power-controller",
                configuration.getName(), "1.4.0", timeStamp,  uptime, info);
    }

}
