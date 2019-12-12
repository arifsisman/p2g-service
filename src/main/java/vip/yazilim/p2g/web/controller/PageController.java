package vip.yazilim.p2g.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static vip.yazilim.p2g.web.constant.Constants.API_SPOTIFY;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "redirect:" + API_SPOTIFY + "/authorize";
    }

}
