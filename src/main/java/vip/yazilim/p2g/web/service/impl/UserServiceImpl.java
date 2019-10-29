package vip.yazilim.p2g.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.p2g.web.repository.IUserRepo;
import vip.yazilim.p2g.web.service.IRoleService;
import vip.yazilim.p2g.web.service.IUserService;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserRepo userRepo;

    @Autowired
    private IRoleService roleService;

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public Optional<UserModel> getUserModelByUuid(String userUuid) throws Exception {
        Optional<User> user;

        try {
            user = userRepo.findByUuid(userUuid);
        } catch (Exception e) {
            throw new Exception("Cannot get user.");
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

    @Override
    public Optional<User> create(User item) throws Exception {
        Optional<User> user;

        try {
            user = Optional.of(userRepo.save(item));
        } catch (Exception e) {
            throw new Exception("Cannot create user.");
        }

        return user;
    }

    @Override
    public Optional<User> getByUuid(String uuid) throws Exception {
        Optional<User> user;

        try {
            user = userRepo.findByUuid(uuid);
        } catch (Exception e) {
            throw new Exception("Cannot read user.");
        }

        return user;
    }

    @Override
    public Optional<User> update(User item) throws Exception {
        Optional<User> user;

        try {
            user = Optional.of(userRepo.save(item));
        } catch (Exception e) {
            throw new Exception("Cannot update user.");
        }

        return user;
    }

    @Override
    public void delete(User item) throws Exception {
        try {
            userRepo.delete(item);
        } catch (Exception e) {
            throw new Exception("Cannot delete user.");
        }
    }

    @Override
    public void delete(String uuid) throws Exception {
        try {
            userRepo.deleteById(uuid);
        } catch (Exception e) {
            throw new Exception("Cannot delete user.");
        }
    }
}
