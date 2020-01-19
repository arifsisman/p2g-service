package vip.yazilim.p2g.web.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import vip.yazilim.p2g.web.config.security.user.CustomUserPrincipal;

import java.util.Map;

public class SecurityHelper {

    // With SecurityContext
    private static OAuth2Authentication getUserAuthentication() {
        return (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
    }

    public static String getUserId() {
        return ((Map) getUserAuthentication().getUserAuthentication().getDetails()).get("id").toString();
    }

    // With Authentication
    public static String getUserId(Authentication authentication) {
        return ((CustomUserPrincipal) authentication.getPrincipal()).getUser().getId();
    }

    public static String getUserDisplayName(Authentication authentication) {
        return ((CustomUserPrincipal) authentication.getPrincipal()).getUser().getDisplayName();
    }

}
