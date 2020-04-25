package itx.rpi.powercontroller.services.impl;

public final class ServiceUtils {

    private ServiceUtils() {
    }

    private static final long MILLIS_DAY = 24*60*60*1000L;
    private static final long MILLIS_HOUR = 60*60*1000L;
    private static final long MILLIS_MINUTE = 60*1000L;
    private static final long MILLIS_SECOND = 1000L;

    public static String calculateUptimeDays(long uptime) {
        long days = uptime / MILLIS_DAY;
        long hours = (uptime - days*MILLIS_DAY) / MILLIS_HOUR;
        long minutes = (uptime - days*MILLIS_DAY - hours*MILLIS_HOUR) / MILLIS_MINUTE;
        long seconds  = (uptime - days*MILLIS_DAY - hours*MILLIS_HOUR - minutes*MILLIS_MINUTE) / MILLIS_SECOND;
        long ms = (uptime - days*MILLIS_DAY - hours*MILLIS_HOUR - minutes*MILLIS_MINUTE - seconds*MILLIS_SECOND);
        return convert(4,days) + " days, " + convert(2,hours) +
                ":" + convert(2,minutes) + ":" + convert(2,seconds) +
                ", " + convert(3,ms) + " ms";
    }

    private static String convert(int decimals, long digit) {
        return String.format("%0" + decimals + "d", digit);
    }

}
