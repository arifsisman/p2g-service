package vip.yazilim.p2g.web.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vip.yazilim.p2g.web.entity.User;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author mustafaarifsisman - 13.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class CustomUserPrincipal implements UserDetails {

    private User user;

    CustomUserPrincipal(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
