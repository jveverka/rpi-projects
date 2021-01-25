package one.microproject.rpi.powercontroller.tests;

import one.microproject.rpi.powercontroller.services.impl.ServiceUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServiceUtilsTests {

    @Test
    public void calculateUptimeDaysTest() {
        String uptime = ServiceUtils.calculateUptimeDays(99L);
        assertEquals("0000 days, 00:00:00, 099 ms", uptime);

        uptime = ServiceUtils.calculateUptimeDays(1000L);
        assertEquals("0000 days, 00:00:01, 000 ms", uptime);

        uptime = ServiceUtils.calculateUptimeDays(30*1000L);
        assertEquals("0000 days, 00:00:30, 000 ms", uptime);

        uptime = ServiceUtils.calculateUptimeDays(30*60*1000L);
        assertEquals("0000 days, 00:30:00, 000 ms", uptime);

        uptime = ServiceUtils.calculateUptimeDays(6*60*60*1000L);
        assertEquals("0000 days, 06:00:00, 000 ms", uptime);

        uptime = ServiceUtils.calculateUptimeDays(8*24*60*60*1000L);
        assertEquals("0008 days, 00:00:00, 000 ms", uptime);

        uptime = ServiceUtils.calculateUptimeDays(600*24*60*60*1000L);
        assertEquals("0600 days, 00:00:00, 000 ms", uptime);

        uptime = ServiceUtils.calculateUptimeDays(387912254L);
        assertEquals("0004 days, 11:45:12, 254 ms", uptime);

        uptime = ServiceUtils.calculateUptimeDays(388319408L);
        assertEquals("0004 days, 11:51:59, 408 ms", uptime);

    }

}
