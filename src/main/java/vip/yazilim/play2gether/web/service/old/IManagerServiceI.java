package vip.yazilim.play2gether.web.service.old;

import vip.yazilim.play2gether.web.entity.old.Manager;
import vip.yazilim.play2gether.web.service.ICrudService;

import java.util.Optional;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
public interface IManagerServiceI extends ICrudService<Manager, String> {

    public Optional<Manager> getManagerBySystemUser(String userUuid);
}
