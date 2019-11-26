//package vip.yazilim.p2g.web.controller;
//
//import com.wrapper.spotify.SpotifyApi;
//import com.wrapper.spotify.exceptions.SpotifyWebApiException;
//import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Controller;
//import vip.yazilim.p2g.web.constant.Constants;
//import vip.yazilim.p2g.web.exception.TokenException;
//import vip.yazilim.p2g.web.service.ITokenService;
//import vip.yazilim.p2g.web.util.SecurityHelper;
//import vip.yazilim.spring.utils.exception.DatabaseException;
//import vip.yazilim.spring.utils.exception.InvalidUpdateException;
//
//import java.io.IOException;
//
//@EnableScheduling
//@Controller
//public class SpotifyTokenRefresher {
//
//    @Autowired
//    @Qualifier(Constants.BEAN_NAME_AUTHORIZATION_CODE)
//    private SpotifyApi spotifyApi;
//
//    @Autowired
//    private ITokenService tokenService;
//
////    @Scheduled(fixedRate = 3000000)
//    @Scheduled(fixedRate = 10000)
//    public void refreshToken() throws DatabaseException, InvalidUpdateException {
//
//        String refreshToken = spotifyApi.getRefreshToken();
//
//        try {
//            if (refreshToken != null) {
//                AuthorizationCodeCredentials authorizationCodeCredentials = spotifyApi.authorizationCodeRefresh()
//                        .build().execute();
//
//                String accessToken = authorizationCodeCredentials.getAccessToken();
//                spotifyApi.setAccessToken(accessToken);
//
//                String userUuid = SecurityHelper.getUserUuid();
//                tokenService.saveUserToken(userUuid, accessToken, refreshToken);
//            }
//        } catch (IOException | SpotifyWebApiException e) {
//            throw new TokenException("Error while getting new access token!");
//        }
//
//    }
//}
