package vip.yazilim.p2g.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.service.IUserService;
import vip.yazilim.spring.utils.exception.DatabaseException;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private IUserService userService;

    @Override
    public void run(String... args) throws Exception {
        createUser("arif", "arif", "0");
        createUser("emre", "emre", "0");

    }

    private User createUser(String email, String username, String password){
        User user = new User();
        user.setEmail(email);
        user.setDisplayName(username);
        user.setPassword(password);

        try {
            userService.create(user);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        return user;
    }

}
