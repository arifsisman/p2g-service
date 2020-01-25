package vip.yazilim.p2g.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/ws")
    public String ws(Model model) {
        return "page_ws";
    }

}
