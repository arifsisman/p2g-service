package vip.yazilim.play2gether.web.config.security;

import vip.yazilim.play2gether.web.entity.old.SystemUser;
import vip.yazilim.play2gether.web.service.old.ISystemUserService;
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
        Optional<SystemUser> systemUserOptional = systemUserService.getUserByEmail(s);
        SystemUser systemUser = systemUserOptional
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        return new UserPrinciple(systemUser);
    }
}
