package vip.yazilim.p2g.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Emre Sen - 25.05.2019
 * @contact maemresen07@gmail.com
 */
@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "redirect:authorize";
    }

    @GetMapping("/profile")
    public String profile() {
        return "page_profile";
    }
}
