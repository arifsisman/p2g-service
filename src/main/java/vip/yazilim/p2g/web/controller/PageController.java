package vip.yazilim.p2g.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.service.p2g.impl.RoomUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;

import java.util.Optional;

@Controller
public class PageController {

    @Autowired
    private RoomUserService roomUserService;

//    @GetMapping("/")
//    public String home() {
//        return "redirect:" + API_SPOTIFY + "/authorize";
//    }

    @GetMapping("/ws")
    public String ws(Model model) throws DatabaseException {
        model.addAttribute("userId", SecurityHelper.getUserId());

        Optional<RoomUser> roomUser = roomUserService.getRoomUser(SecurityHelper.getUserId());
        roomUser.ifPresent(user -> model.addAttribute("roomId", user.getRoomId()));

        return "page_ws";
    }

}
