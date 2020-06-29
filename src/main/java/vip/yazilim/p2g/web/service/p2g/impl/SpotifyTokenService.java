package vip.yazilim.p2g.web.service.p2g.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.libs.springcore.exception.DatabaseReadException;
import vip.yazilim.libs.springcore.service.ACrudServiceImpl;
import vip.yazilim.p2g.web.entity.OAuthToken;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.UserDevice;
import vip.yazilim.p2g.web.repository.ISpotifyTokenRepo;
import vip.yazilim.p2g.web.service.p2g.ISpotifyTokenService;
import vip.yazilim.p2g.web.service.p2g.IUserDeviceService;
import vip.yazilim.p2g.web.service.p2g.IUserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 31.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SpotifyTokenService extends ACrudServiceImpl<OAuthToken, String> implements ISpotifyTokenService {

    private final ISpotifyTokenRepo tokenRepo;
    private final IUserService userService;
    private final IUserDeviceService userDeviceService;

    public SpotifyTokenService(ISpotifyTokenRepo tokenRepo, @Lazy IUserService userService, IUserDeviceService userDeviceService) {
        this.tokenRepo = tokenRepo;
        this.userService = userService;
        this.userDeviceService = userDeviceService;
    }

    @Override
    protected JpaRepository<OAuthToken, String> getRepository() {
        return tokenRepo;
    }

    @Override
    protected String getId(OAuthToken entity) {
        return entity.getUserId();
    }

    @Override
    protected Class<OAuthToken> getClassOfEntity() {
        return OAuthToken.class;
    }

    @Override
    public Optional<OAuthToken> getTokenByUserId(String userId) {
        try {
            return tokenRepo.findOAuthTokenByUserId(userId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, userId);
        }
    }

    @Override
    public String saveUserToken(String userId, String accessToken) {
        Optional<OAuthToken> spotifyToken = getTokenByUserId(userId);

        if (spotifyToken.isPresent()) {
            OAuthToken token = spotifyToken.get();
            token.setAccessToken(accessToken);
            return update(token).getAccessToken();
        }

        OAuthToken entity = new OAuthToken();
        entity.setUserId(userId);
        entity.setAccessToken(accessToken);
        return create(entity).getAccessToken();
    }

    @Override
    public Map<String, String> getRoomTokenDeviceMap(Long roomId) {
        Map<String, String> map = new HashMap<>();

        List<User> userList = userService.getUsersByRoomId(roomId);

        for (User u : userList) {
            String userId = u.getId();
            Optional<OAuthToken> token = getTokenByUserId(userId);
            Optional<UserDevice> userDeviceOpt = userDeviceService.getUsersActiveDevice(userId);

            if (token.isPresent() && userDeviceOpt.isPresent()) {
                map.put(token.get().getAccessToken(), userDeviceOpt.get().getId());
            }
        }

        return map;
    }
}
