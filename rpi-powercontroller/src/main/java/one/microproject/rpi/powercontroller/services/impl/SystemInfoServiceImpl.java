package one.microproject.rpi.powercontroller.services.impl;

import one.microproject.rpi.powercontroller.config.Configuration;
import one.microproject.rpi.powercontroller.dto.SystemInfo;
import one.microproject.rpi.powercontroller.services.SystemInfoService;

import java.util.Date;

public class SystemInfoServiceImpl implements SystemInfoService {

    private final Configuration configuration;

    public SystemInfoServiceImpl(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SystemInfo getSystemInfo() {
        long uptime = new Date().getTime() - configuration.getStarted().getTime();
        return new SystemInfo(configuration.getId(), "power-controller",
                configuration.getName(), "1.0.0", configuration.isHardware(), configuration.getStarted(), uptime,
                ServiceUtils.calculateUptimeDays(uptime));
    }

}
