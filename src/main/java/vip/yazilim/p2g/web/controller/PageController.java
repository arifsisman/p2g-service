package vip.yazilim.p2g.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import vip.yazilim.p2g.web.spotify.IPlayer;

@Controller
public class PageController {

    @Autowired
    IPlayer player;

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
}
