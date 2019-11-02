package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.relation.RoomInvite;
import vip.yazilim.p2g.web.exception.DatabaseException;

/**
 * @author mustafaarifsisman - 2.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomInviteService extends ICrudService<RoomInvite, String> {

    boolean inviteUserToRoom(String roomUuid, String userUuid) throws DatabaseException;
    boolean replyInviteByUuid(String roomInviteUuid, boolean isAccepted) throws DatabaseException;

}