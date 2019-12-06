package vip.yazilim.p2g.web.rest.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.yazilim.p2g.web.entity.relation.RoomInvite;
import vip.yazilim.p2g.web.rest.IRoomInviteRest;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomInviteService;
import vip.yazilim.spring.core.rest.ARestCrud;
import vip.yazilim.spring.core.service.ICrudService;

import static vip.yazilim.p2g.web.constant.Constants.API_P2G;

/**
 * @author mustafaarifsisman - 07.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_P2G + "/invite")
public class RoomInviteRest extends ARestCrud<RoomInvite, String> implements IRoomInviteRest {

    @Autowired
    private IRoomInviteService roomInviteService;

    @Override
    protected ICrudService<RoomInvite, String> getService() {
        return roomInviteService;
    }
}
