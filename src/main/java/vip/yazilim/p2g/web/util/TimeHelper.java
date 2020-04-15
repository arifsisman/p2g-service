package vip.yazilim.p2g.web.util;


import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class TimeHelper {

    public static LocalDateTime getLocalDateTimeNow() {
        return LocalDateTime.now();
    }

    public static Date getDateNow() {
        return new Date();
    }

    public static Date getDatePostponed(Integer delayMs) {
        return new Date(getDateNow().getTime() + delayMs);
    }

}
