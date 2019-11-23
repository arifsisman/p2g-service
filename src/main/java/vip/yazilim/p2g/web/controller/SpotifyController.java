package vip.yazilim.p2g.web.controller;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import vip.yazilim.p2g.web.constant.Constants;
import vip.yazilim.p2g.web.spotify.flow.AuthorizationCodeUri;

import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URI;

/**
 * @author mustafaarifsisman - 23.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Controller
public class SpotifyController {

    private final Logger LOGGER  = LoggerFactory.getLogger(SpotifyApi.class);
//    @Autowired
//    @Qualifier(Constants.BEAN_NAME_AUTHORIZATION_CODE)
//    private SpotifyApi spotifyApi;

    @Autowired
    private AuthorizationCodeUriRequest authorizationCodeUriRequest;

    @GetMapping("/authorize")
    public void authorize(HttpServletResponse httpServletResponse) {
        URI uri = authorizationCodeUriRequest.execute();

        httpServletResponse.setHeader("Location", uri.toString());
        httpServletResponse.setStatus(302);
    }

    @GetMapping("/callback")
    @ResponseBody
    public String callback(@RequestParam String code){
        //play2gether://callback/?code=AQDYiF6yNSfQ-zF6Z7OL4_kQo64LI3J-RlZnWSeW9LRjWRNcdlg3wUV3DEZhn7ZB93wmmTvHwKo9KLt
        // MprbR2sUXfwlsvx2zrhsPhrqR9HY2nbnEUXBoQ9vW2ucGA78wY8d2dFW5kqHWpVISFKIfNXc9LkD31KemBWZaoRJYlXeFpL_JO898tFgRoHw

        LOGGER.info("Code: " + code);

        return code;
    }

}
