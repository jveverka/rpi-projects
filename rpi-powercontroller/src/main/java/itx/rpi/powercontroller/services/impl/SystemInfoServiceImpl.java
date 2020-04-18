package itx.rpi.powercontroller.services.impl;

import itx.rpi.powercontroller.config.Configuration;
import itx.rpi.powercontroller.dto.SystemInfo;
import itx.rpi.powercontroller.services.SystemInfoService;

public class SystemInfoServiceImpl implements SystemInfoService {

    private final Configuration configuration;

    public SystemInfoServiceImpl(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SystemInfo getSystemInfo() {
        return new SystemInfo(configuration.getId(), "power-controller",
                configuration.getName(), "1.0.0", configuration.isHardware());
    }

}
