package vip.yazilim.p2g.web.model.service;

import lombok.Data;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.entity.relation.UserDevice;

import java.util.List;

/**
 * @author mustafaarifsisman - 04.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Data
public class PlayerModel {
    private String roomUuid;
    private List<SpotifyToken> spotifyTokenList;
    private List<UserDevice> userDeviceList;
}
