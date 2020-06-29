package vip.yazilim.p2g.web.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.util.Map;

public class SecurityHelper {
    private SecurityHelper() {
    }

    private static OAuth2Authentication getUserAuthentication() {
        return (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
    }

    public static String getUserId() {
        return getAuthorizedMap().get("id").toString();
    }

    public static String getUserDisplayName() {
        return getAuthorizedMap().get("display_name").toString();
    }

    public static String getUserEmail() {
        return getAuthorizedMap().get("email").toString();
    }

    public static String getUserAccessToken() {
        return ((OAuth2AuthenticationDetails) getUserAuthentication().getDetails()).getTokenValue();
    }

    public static Map<String, Object> getAuthorizedMap() {
        return (Map<String, Object>) getUserAuthentication().getUserAuthentication().getDetails();
    }
}
