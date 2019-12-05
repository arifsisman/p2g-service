package vip.yazilim.p2g.web.controller.spotify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vip.yazilim.p2g.web.entity.relation.UserDevice;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.core.exception.web.ServiceException;

import java.util.List;

/**
 * @author mustafaarifsisman - 1.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
public class SpotifyUserController {

    @Autowired
    private ISpotifyUserService spotifyUserService;

    @GetMapping("/s/user/{spotifyAccountId}")
    public com.wrapper.spotify.model_objects.specification.User getSpotifyUser(@PathVariable String spotifyAccountId) {
        try {
            return spotifyUserService.getSpotifyUser(spotifyAccountId);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @GetMapping("/s/user/current")
    public com.wrapper.spotify.model_objects.specification.User getCurrentSpotifyUser() {
        try {
            return spotifyUserService.getCurrentSpotifyUser(SecurityHelper.getUserUuid());
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @GetMapping("/s/user/devices")
    public List<UserDevice> getSpotifyUserDevices() {
        try {
            return spotifyUserService.getUsersAvailableDevices(SecurityHelper.getUserUuid());
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
}
