package vip.yazilim.p2g.web.util;

import vip.yazilim.p2g.web.config.security.UserPrinciple;
import vip.yazilim.p2g.web.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Emre Sen - 25.05.2019
 * @contact maemresen07@gmail.com
 */
public class SecurityHelper {

    public static UserPrinciple getUserPrinciple() {
        return (UserPrinciple) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    public static User getUser() {
        return getUserPrinciple().getUser();
    }

    public static String getUserUuid() {
        return getUser().getUuid();
    }

    public static boolean hasAnyRole(String... roleNames) {
        for (String roleName : roleNames) {
            if (hasRole(roleName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasRole(String roleName) {
        for (GrantedAuthority grantedAuthority : getUserPrinciple().getAuthorities()) {
            String authority = grantedAuthority.getAuthority();
            System.out.println(String.format("Comparing: Authority: %s, Role: %s", authority, roleName));
            if (authority.equals(roleName)) {
                System.out.println("Matched Authority: " + authority);
                return true;
            }
        }
        return false;
    }
}
