package vip.yazilim.p2g.web.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

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

    public static String getUserImageUrl() {
        //todo fix
        return ((Map) getUserAuthentication().getUserAuthentication().getDetails()).get("images").toString();
    }

    public static String getUserEmail() {
        return ((Map) getUserAuthentication().getUserAuthentication().getDetails()).get("email").toString();
    }

    public static String getUserAccessToken() {
        return ((OAuth2AuthenticationDetails) getUserAuthentication().getDetails()).getTokenValue();
    }

    // With Authentication
    public static String getUserId(OAuth2Authentication oAuth2Authentication) {
        return ((Map) oAuth2Authentication.getUserAuthentication().getDetails()).get("id").toString();
    }

    public static String getUserDisplayName(OAuth2Authentication oAuth2Authentication) {
        return ((Map) oAuth2Authentication.getUserAuthentication().getDetails()).get("display_name").toString();
    }

}
