package vip.yazilim.p2g.web.util;

import java.util.UUID;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
public class DBHelper {

    public static String getRandomUuid(){
        return UUID.randomUUID().toString();
    }
}
