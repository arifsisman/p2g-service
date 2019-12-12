package vip.yazilim.p2g.web.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import vip.yazilim.p2g.web.entity.User;

public class SecurityHelper {

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static User getUser() {
        return (User) getAuthentication().getPrincipal();
    }

    public static String getUserUuid() {
        return getUser().getUuid();
    }
}
