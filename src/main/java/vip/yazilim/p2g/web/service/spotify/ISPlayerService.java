package vip.yazilim.p2g.web.service.spotify;

import vip.yazilim.p2g.web.entity.relation.UserDevice;

import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISPlayerService {

    void play(String roomUuid, String songUri);
    void play(String roomUuid);
    void pause(String roomUuid);
    void next(String roomUuid);
    void previous(String roomUuid);

    void seek(String roomUuid, Integer ms);
    void repeat(String roomUuid);

    List<UserDevice> getUsersAvailableDevices(String userUuid);

}
