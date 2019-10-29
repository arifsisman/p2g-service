package vip.yazilim.p2g.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.repository.IUserRepo;
import vip.yazilim.p2g.web.service.IRoleService;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.p2g.web.service.IUserService;

import java.util.Optional;

public class UserServiceImpl implements IUserService {


    @Autowired
    private IUserRepo userRepo;

    @Autowired
    private IRoleService roleService;

    @Override
    public Optional<User> getUserByEmail(String email) {
        //TODO: impl unutma
        return null;
    }

    @Override
    public Optional<UserModel> getUserModelByUuid(String userUuid) throws Exception {
        Optional<User> user;

        try {
            user = userRepo.findByUuid(userUuid);
        } catch (Exception e) {
            throw new Exception("my exception");
        }

        if (!user.isPresent()) {
            return Optional.empty();
        }

        UserModel userModel = new UserModel();
        userModel.setUser(user.get());

        // User Role
        Optional<Role> role = roleService.getRoleByUuid(userUuid);
        if (role.isPresent()) {
            userModel.setRole(role.get());
        } else {
            Role defaultRole = roleService.getDefaultRole();
            userModel.setRole(defaultRole);
        }

        return Optional.of(userModel);
    }

}
