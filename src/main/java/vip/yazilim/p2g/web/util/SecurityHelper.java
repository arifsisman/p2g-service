package vip.yazilim.p2g.web.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Map;

public class SecurityHelper {

    // With SecurityContext
    private static OAuth2Authentication getUserAuthentication() {
        return (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
    }

    public static String getUserId() {
        return ((Map) getUserAuthentication().getUserAuthentication().getDetails()).get("id").toString();
    }

    public static String getUserDisplayName() {
        return ((Map) getUserAuthentication().getUserAuthentication().getDetails()).get("display_name").toString();
    }

    // With Authentication
    public static String getUserId(OAuth2Authentication oAuth2Authentication) {
        return ((Map) oAuth2Authentication.getUserAuthentication().getDetails()).get("id").toString();
    }

    public static String getUserDisplayName(OAuth2Authentication oAuth2Authentication) {
        return ((Map) oAuth2Authentication.getUserAuthentication().getDetails()).get("display_name").toString();
    }

}
