package vip.yazilim.p2g.web.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import vip.yazilim.p2g.web.config.security.user.CustomUserPrincipal;
import vip.yazilim.p2g.web.entity.User;

public class SecurityHelper {

    private static CustomUserPrincipal getUserPrinciple() {
        return (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Authentication getUserAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static User getUser() {
        return getUserPrinciple().getUser();
    }

    public static String getUserUuid() {
        return getUser().getUuid();
    }

}
