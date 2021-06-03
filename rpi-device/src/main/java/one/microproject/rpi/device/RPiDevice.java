package one.microproject.rpi.device;

import one.microproject.rpi.device.dto.SystemInfo;

public interface RPiDevice<T> {

    SystemInfo<T> getSystemInfo();

}
