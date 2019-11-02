package vip.yazilim.p2g.web.config.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import vip.yazilim.p2g.web.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.exception.DatabaseException;
import vip.yazilim.p2g.web.service.IUserService;

import java.util.Optional;

/**
 * @author Emre Sen - 25.05.2019
 * @contact maemresen07@gmail.com
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IUserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> userOptional = null;

        try {
            userOptional = userService.getUserByEmail(s);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        User user = userOptional
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        return new UserPrinciple(user);
    }
}
