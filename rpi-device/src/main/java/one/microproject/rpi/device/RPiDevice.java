package one.microproject.rpi.device;

import one.microproject.rpi.device.dto.SystemInfo;

/**
 * Generic RaspberryPi device.
 * @param <T> system info properties type.
 */
public interface RPiDevice<T> {

    /**
     * Get device's system info.
     * @return current system state of the device.
     */
    SystemInfo<T> getSystemInfo();

}
