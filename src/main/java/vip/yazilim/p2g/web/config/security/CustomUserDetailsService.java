package vip.yazilim.p2g.web.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.p2g.relation.IPrivilegeService;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.general.database.DatabaseReadException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IUserService userService;

    @Autowired
    private IPrivilegeService privilegeService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = Optional.empty();

        try {
            userOptional = userService.getUserByEmail(email);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));
        return new CustomUserPrincipal(user);
    }

    public Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) throws DatabaseReadException {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role: roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
            privilegeService.getPrivilegeList(role.getName()).stream()
                    .map(p -> new SimpleGrantedAuthority(p.getName()))
                    .forEach(authorities::add);
        }

        return authorities;
    }

//    public Collection<? extends GrantedAuthority> getUserAuthorities(Collection<String> roles) throws DatabaseReadException {
//        return getGrantedAuthorities(getPrivileges(roles));
//    }
//
////    public Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) throws DatabaseReadException {
////        return getGrantedAuthorities(getPrivileges(roles));
////    }
//
//    public List<String> getPrivileges(Collection<String> roleNames) throws DatabaseReadException {
//        List<String> privileges = new ArrayList<>();
//        List<Privilege> collection = new ArrayList<>();
//        for (String rl : roleNames) {
//            collection.addAll(privilegeService.getPrivilegeList(rl));
//        }
//        for (Privilege p : collection) {
//            privileges.add(p.getPrivilegeName());
//        }
//        return privileges;
//    }
//
//    public List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
//        List<GrantedAuthority> authorities = new ArrayList<>();
//        for (String privilege : privileges) {
//            authorities.add(new SimpleGrantedAuthority(privilege));
//        }
//        return authorities;
//    }

}
