package vip.yazilim.p2g.web.config.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import vip.yazilim.p2g.web.config.annotation.*;
import vip.yazilim.p2g.web.controller.WebSocketController;
import vip.yazilim.p2g.web.exception.ForbiddenException;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.p2g.web.service.p2g.ISongService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;

import java.lang.annotation.Annotation;

/**
 * @author mustafaarifsisman - 16.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Configuration
@Aspect
public class RestAspect {

    private static final String ASPECT_PACKAGE_PATTERN = "execution(* vip.yazilim.p2g.web.rest.*.*(..))";

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

    /**
     * to be executed before invoking methods which matches given pattern before
     * executed
     *
     * @param jpoint point where aspect joins
     */
    @Before(ASPECT_PACKAGE_PATTERN)
    public void before(JoinPoint jpoint) {
        for (Annotation annotation : ((MethodSignature) jpoint.getSignature()).getMethod().getDeclaredAnnotations()) {
            if (annotation instanceof HasRoomPrivilege) {
                handle((HasRoomPrivilege) annotation);
            } else if (annotation instanceof HasSystemRole) {
                handle((HasSystemRole) annotation);
            } else if (annotation instanceof HasRoomRole) {
                handle((HasRoomRole) annotation);
            } else if (annotation instanceof HasSystemPrivilege) {
                handle((HasSystemPrivilege) annotation);
            }
        }
    }

    /**
     * to be executed after invoking methods which matches given pattern before
     * executed
     *
     * @param jpoint point where aspect joins
     */
    @After(ASPECT_PACKAGE_PATTERN)
    public void after(JoinPoint jpoint) {
        for (Annotation annotation : ((MethodSignature) jpoint.getSignature()).getMethod().getDeclaredAnnotations()) {
            if (annotation instanceof UpdateRoomSongs) {
                handleUpdateRoomSongs();
            } else if (annotation instanceof UpdateRoomUsers) {
                handleUpdateRoomUsers();
            }
        }
    }


    // Handle by Privileges
    private void handle(HasRoomPrivilege hasRoomPrivilege) {
        if (!roomUserService.hasRoomPrivilege(SecurityHelper.getUserId(), hasRoomPrivilege.privilege())) {
            throw new ForbiddenException("Insufficient Room Privileges");
        }
    }

    private void handle(HasSystemPrivilege hasRoomPrivilege) {
        if (!userService.hasSystemPrivilege(SecurityHelper.getUserId(), hasRoomPrivilege.privilege())) {
            throw new ForbiddenException("Insufficient System Privileges");
        }
    }


    // Handle by Roles
    private void handle(HasRoomRole hasRoomRole) {
        if (!roomUserService.hasRoomRole(SecurityHelper.getUserId(), hasRoomRole.role())) {
            throw new ForbiddenException("Insufficient Room Role");
        }
    }

    private void handle(HasSystemRole hasSystemRole) {
        if (!userService.hasSystemRole(SecurityHelper.getUserId(), hasSystemRole.role())) {
            throw new ForbiddenException("Insufficient System Role");
        }
    }


    // Handle room events
    private void handleUpdateRoomSongs() {
        roomUserService.getRoomUserByUserId(SecurityHelper.getUserId())
                .ifPresent(roomUser -> webSocketController.sendToRoom("songs", roomUser.getRoomId(),
                        songService.getSongListByRoomId(roomUser.getRoomId(), false)));
    }

    private void handleUpdateRoomUsers() {
        roomUserService.getRoomUserByUserId(SecurityHelper.getUserId())
                .ifPresent(roomUser -> webSocketController.sendToRoom("users", roomUser.getRoomId(),
                        roomUserService.getRoomUserModelsByRoomId(roomUser.getRoomId())));
    }
}
