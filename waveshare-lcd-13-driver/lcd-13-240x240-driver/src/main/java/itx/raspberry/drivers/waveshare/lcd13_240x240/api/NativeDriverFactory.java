package itx.raspberry.drivers.waveshare.lcd13_240x240.api;

import itx.raspberry.drivers.waveshare.lcd13_240x240.NativeDriverImpl;
import itx.raspberry.drivers.waveshare.lcd13_240x240.NativeDriverLowLevel;
import itx.raspberry.drivers.waveshare.lcd13_240x240.NativeDriverSimulatedImpl;

public class NativeDriverFactory {

    public static NativeDriver getNativeDriver(boolean simulated) {
        if (simulated) {
            return new NativeDriverSimulatedImpl();
        } else {
            return new NativeDriverImpl(new NativeDriverLowLevel());
        }
    }

}
