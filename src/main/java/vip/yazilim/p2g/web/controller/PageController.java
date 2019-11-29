package vip.yazilim.p2g.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vip.yazilim.p2g.web.constant.SearchTypes;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.ISearchService;
import vip.yazilim.p2g.web.service.ITokenService;
import vip.yazilim.p2g.web.spotify.IPlayer;

import java.util.List;

@Controller
public class PageController {

    @Autowired
    private IPlayer player;

    @Autowired
    private ISearchService searchService;

    @Autowired
    private ITokenService tokenService;

    private final Logger LOGGER = LoggerFactory.getLogger(PageController.class);

    @GetMapping("/")
    public String home() {
        return "redirect:authorize";
    }

    @GetMapping("/profile")
    public String profile() {
        return "page_profile";
    }

    @GetMapping("/play")
    public String play() {
        player.play("1", "spotify:album:5zT1JLIj9E57p3e1rFm9Uq");
        return "home";
    }

    @GetMapping("/search/{query}")
    public String search(@PathVariable String query) {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid("1");
        SpotifyToken spotifyToken = null;

        if (!spotifyTokenList.isEmpty()) {
            spotifyToken = spotifyTokenList.get(0);
            LOGGER.info("Token: " + spotifyToken);
        } else {
            LOGGER.info("Token is null");
        }

//        List<SearchModel> searchModelList = searchService.search(spotifyToken, query, SearchTypes.TRACK);
        List<SearchModel> searchModelList = searchService.search(spotifyToken, query);

        for (SearchModel searchModel : searchModelList) {
            LOGGER.info(searchModel.getType().getType());
            LOGGER.info(searchModel.getName());
        }

        return "home";
    }
}
