package vip.yazilim.play2gether.web.service;

import vip.yazilim.play2gether.web.entity.Manager;

import java.util.Optional;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
public interface IManagerService extends CrudService<Manager, String> {

    public Optional<Manager> getManagerBySystemUser(String userUuid);
}
