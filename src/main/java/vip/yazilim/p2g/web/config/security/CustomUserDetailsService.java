package vip.yazilim.p2g.web.config.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import vip.yazilim.p2g.web.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.service.IUserService;
import vip.yazilim.spring.utils.exception.DatabaseException;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IUserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = Optional.empty();

        try {
            userOptional = userService.getUserByEmail(email);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));

        return new UserPrinciple(user);
    }
}
