package vip.yazilim.p2g.web.util;

import java.util.UUID;

public class DBHelper {
    public static String getRandomUuid(){
        return UUID.randomUUID().toString();
    }
}
