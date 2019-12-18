package vip.yazilim.p2g.web.service.spotify.model;

import lombok.Data;

import java.util.List;

/**
 * @author mustafaarifsisman - 04.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Data
public class PlayerModel {
    //todo: hashmap
    private String roomUuid;
    private List<String> spotifyTokenList;
    private List<String> userDeviceList;
}
