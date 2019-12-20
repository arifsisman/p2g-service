package vip.yazilim.p2g.web.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class TimeHelper {

    public static Long getCurrentTimeMsLong() {
        return ZonedDateTime.of(getLocalDateTimeNow(), ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static int getCurrentTimeMsInt() {
        return (int) ZonedDateTime.of(getLocalDateTimeNow(), ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDateTime getLocalDateTimeNow() {
        return LocalDateTime.now();
    }

}
