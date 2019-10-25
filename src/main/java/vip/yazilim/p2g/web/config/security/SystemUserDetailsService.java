package vip.yazilim.p2g.web.config.security;

import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.service.ISystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Emre Sen - 25.05.2019
 * @contact maemresen07@gmail.com
 */
@Service
public class SystemUserDetailsService implements UserDetailsService {

    @Autowired
    private ISystemUserService systemUserService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> systemUserOptional = systemUserService.getUserByEmail(s);
        User user = systemUserOptional
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        return new UserPrinciple(user);
    }
}
