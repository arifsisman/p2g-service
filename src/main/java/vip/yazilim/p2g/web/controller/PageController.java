package vip.yazilim.p2g.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.service.p2g.impl.relation.RoomUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;

import java.util.Optional;

import static vip.yazilim.p2g.web.constant.Constants.API_SPOTIFY;

@Controller
public class PageController {

    @Autowired
    private RoomUserService roomUserService;

    @GetMapping("/")
    public String home() {
        return "redirect:" + API_SPOTIFY + "/authorize";
    }

    @GetMapping("/ws")
    public String ws(Model model) throws DatabaseException {
        model.addAttribute("userUuid", SecurityHelper.getUserUuid());

        Optional<RoomUser> roomUser = roomUserService.getRoomUser(SecurityHelper.getUserUuid());
        roomUser.ifPresent(user -> model.addAttribute("roomUuid", user.getRoomUuid()));

        return "page_ws";
    }

}
