package vip.yazilim.play2gether.web.service;

import vip.yazilim.play2gether.web.entity.SystemUser;

import java.util.Optional;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
public interface ISystemUserService extends CrudService<SystemUser, String> {

    public Optional<SystemUser> getUserByEmail(String email);

}
