package vip.yazilim.p2g.web.config.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import vip.yazilim.p2g.web.config.annotation.*;
import vip.yazilim.p2g.web.controller.WebSocketController;
import vip.yazilim.p2g.web.exception.ForbiddenException;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.p2g.web.service.p2g.ISongService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;

/**
 * @author mustafaarifsisman - 16.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Configuration
@Aspect
public class RestAspect {

    private final IRoomUserService roomUserService;
    private final IUserService userService;
    private final ISongService songService;
    private final WebSocketController webSocketController;

    public RestAspect(IRoomUserService roomUserService, IUserService userService, ISongService songService, WebSocketController webSocketController) {
        this.roomUserService = roomUserService;
        this.userService = userService;
        this.songService = songService;
        this.webSocketController = webSocketController;
    }

    // Handle by Privileges
    @Before("@annotation(hasRoomPrivilege)")
    public void handle(HasRoomPrivilege hasRoomPrivilege) {
        if (!roomUserService.hasRoomPrivilege(SecurityHelper.getUserId(), hasRoomPrivilege.privilege())) {
            throw new ForbiddenException("Insufficient Room Privileges");
        }
    }

    @Before("@annotation(hasSystemPrivilege)")
    private void handle(HasSystemPrivilege hasSystemPrivilege) {
        if (!userService.hasSystemPrivilege(SecurityHelper.getUserId(), hasSystemPrivilege.privilege())) {
            throw new ForbiddenException("Insufficient System Privileges");
        }
    }


    // Handle by Roles
    @Before("@annotation(hasRoomRole)")
    private void handle(HasRoomRole hasRoomRole) {
        if (!roomUserService.hasRoomRole(SecurityHelper.getUserId(), hasRoomRole.role())) {
            throw new ForbiddenException("Insufficient Room Role");
        }
    }

    @Before("@annotation(hasSystemRole)")
    private void handle(HasSystemRole hasSystemRole) {
        if (!userService.hasSystemRole(SecurityHelper.getUserId(), hasSystemRole.role())) {
            throw new ForbiddenException("Insufficient System Role");
        }
    }


    // Handle room events
    @After("@annotation(updateRoomSongs)")
    private void handleUpdateRoomSongs(UpdateRoomSongs updateRoomSongs) {
        roomUserService.getRoomUserByUserId(SecurityHelper.getUserId())
                .ifPresent(roomUser -> webSocketController.sendToRoom("songs", roomUser.getRoomId(),
                        songService.getSongListByRoomId(roomUser.getRoomId(), false)));
    }

    @After("@annotation(updateRoomUsers)")
    private void handleUpdateRoomUsers(UpdateRoomUsers updateRoomUsers) {
        roomUserService.getRoomUserByUserId(SecurityHelper.getUserId())
                .ifPresent(roomUser -> webSocketController.sendToRoom("users", roomUser.getRoomId(),
                        roomUserService.getRoomUserModelsByRoomId(roomUser.getRoomId())));
    }
}
