package vip.yazilim.p2g.web.service.p2g.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.repository.relation.IRoomUserRepo;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomUserService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 2.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class RoomUserService extends ACrudServiceImpl<RoomUser, String> implements IRoomUserService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(RoomUserService.class);

    // injected dependencies
    @Autowired
    private IRoomUserRepo roomUserRepo;

    @Override
    protected JpaRepository<RoomUser, String> getRepository() {
        return roomUserRepo;
    }

    @Override
    protected String getId(RoomUser entity) {
        return entity.getUuid();
    }

    @Override
    protected RoomUser preInsert(RoomUser entity) {
        entity.setUuid(DBHelper.getRandomUuid());
        return entity;
    }

    @Override
    public List<RoomUser> getRoomUsersByRoomUuid(String roomUuid) throws DatabaseException {
        try {
            return roomUserRepo.findRoomUserByRoomUuidOrderByUuid(roomUuid);
        } catch (Exception exception) {
            String errMsg = String.format("An error occurred while getting RoomUser with Room:[%s]", roomUuid);
            throw new DatabaseException(errMsg, exception);
        }
    }

    @Override
    public Optional<RoomUser> getRoomUserByUserUuid(String userUuid) throws DatabaseException {
        try {
            return roomUserRepo.findRoomUserByUserUuid(userUuid);
        } catch (Exception exception) {
            String errMsg = String.format("An error occurred while getting RoomUser with User:[%s]", userUuid);
            throw new DatabaseException(errMsg, exception);
        }
    }

    @Override
    public Optional<RoomUser> getRoomUser(String roomUuid, String userUuid) throws DatabaseException {
        try {
            return roomUserRepo.findRoomUserByRoomUuidAndUserUuid(roomUuid, userUuid);
        } catch (Exception exception) {
            String errMsg = String.format("An error occurred while getting RoomUser with Room:[%s], User:[%s]", roomUuid,userUuid);
            throw new DatabaseException(errMsg, exception);
        }
    }

}
