package itx.raspberry.drivers.waveshare.lcd13_240x240.test;

import itx.raspberry.drivers.waveshare.lcd13_240x240.api.AlreadyInitializedException;
import itx.raspberry.drivers.waveshare.lcd13_240x240.api.NativeDriver;
import itx.raspberry.drivers.waveshare.lcd13_240x240.api.NativeDriverFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestNativeDriverSimulated {

    @Test
    public void testDriver() {
        NativeDriver nativeDriver = NativeDriverFactory.getNativeDriver(true);
        try {
            nativeDriver.init(0, 9000000);
        } catch (AlreadyInitializedException e) {
            Assert.fail();
        }
        try {
            nativeDriver.init(0, 9000000);
            Assert.fail();
        } catch (AlreadyInitializedException e) {
        }
    }

}
