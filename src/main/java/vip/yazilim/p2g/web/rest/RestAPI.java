package vip.yazilim.p2g.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.yazilim.p2g.web.model.SystemUserModel;
import vip.yazilim.p2g.web.service.ISystemUserService;

/**
 * @author Emre Sen - 25.05.2019
 * @contact maemresen07@gmail.com
 */

@RestController
@RequestMapping("/api")
public class RestAPI {

    private Logger LOGGER = LoggerFactory.getLogger(RestAPI.class);

    @Autowired
    private ISystemUserService systemUserService;

    public SystemUserModel getSystemUser(String userUuid){
        return systemUserService.getSystemUserModelByUuid(userUuid).get();
    }
}
