package vip.yazilim.p2g.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.service.IRoleService;
import vip.yazilim.p2g.web.service.ISystemUserService;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.p2g.web.repository.ISystemUserRepo;

import java.util.Optional;

public class UserServiceImpl implements ISystemUserService {


    @Autowired
    private ISystemUserRepo systemUserRepo;

    @Autowired
    private IRoleService systemRoleService;

    @Override
    public Optional<User> getUserByEmail(String email) {
        //TODO: impl unutma
        return null;
    }

    @Override
    public Optional<UserModel> getSystemUserModelByUuid(String systemUserUuid) throws Exception {
        Optional<User> systemUser;

        try {
            systemUser = systemUserRepo.findByUuid(systemUserUuid);
        } catch (Exception e) {
            throw new Exception("my exception");
        }

        if (!systemUser.isPresent()) {
            return Optional.empty();
        }

        UserModel userModel = new UserModel();
        userModel.setUser(systemUser.get());

        // User Role
        Optional<Role> systemRole = systemRoleService.getSystemRoleByUuid(systemUserUuid);
        if (systemRole.isPresent()) {
            userModel.setRole(systemRole.get());
        } else {
            Role defaultRole = systemRoleService.getDefaultRole();
            userModel.setRole(defaultRole);
        }

        return Optional.of(userModel);
    }

}
