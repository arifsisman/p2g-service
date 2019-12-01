package vip.yazilim.p2g.web.service.spotify;

import vip.yazilim.p2g.web.entity.relation.UserDevice;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.p2g.web.exception.TokenException;
import vip.yazilim.spring.utils.exception.DatabaseException;

import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyPlayerService {

    void play(String roomUuid, String songUri) throws RequestException;

    void play(String roomUuid) throws RequestException;

    void pause(String roomUuid) throws RequestException;

    void next(String roomUuid) throws RequestException;

    void previous(String roomUuid) throws RequestException;

    void seek(String roomUuid, Integer ms) throws RequestException;

    void repeat(String roomUuid) throws RequestException;

    List<UserDevice> getUsersAvailableDevices(String userUuid) throws DatabaseException, TokenException, RequestException;

}
