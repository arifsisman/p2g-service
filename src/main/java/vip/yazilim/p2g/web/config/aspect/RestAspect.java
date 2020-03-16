package vip.yazilim.p2g.web.config.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import vip.yazilim.p2g.web.config.annotation.*;
import vip.yazilim.p2g.web.constant.enums.Privilege;
import vip.yazilim.p2g.web.constant.enums.Role;
import vip.yazilim.p2g.web.controller.websocket.WebSocketController;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.exception.ForbiddenException;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.p2g.web.service.p2g.ISongService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 16.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Configuration
@Aspect
public class RestAspect {

    private static final String ASPECT_PACKAGE_PATTERN = "execution(* vip.yazilim.p2g.web.controller.rest.*.*.*(..))";
    private final Logger LOGGER = LoggerFactory.getLogger(RestAspect.class);

    @Autowired
    private IRoomUserService roomUserService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ISongService songService;

    @Autowired
    private WebSocketController webSocketController;

    /**
     * to be executed before invoking methods which matches given pattern before
     * executed
     *
     * @param jpoint point where aspect joins
     */
    @Before(ASPECT_PACKAGE_PATTERN)
    public void before(JoinPoint jpoint) {

        MethodSignature signature = (MethodSignature) jpoint.getSignature();
        Method method = signature.getMethod();
        for (Annotation annotation : method.getDeclaredAnnotations()) {
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
        MethodSignature signature = (MethodSignature) jpoint.getSignature();
        Method method = signature.getMethod();
        for (Annotation annotation : method.getDeclaredAnnotations()) {
            if (annotation instanceof UpdateRoomSongs) {
                handleUpdateRoomSongs();
            } else if (annotation instanceof UpdateRoomUsers) {
                handleUpdateRoomUsers();
            }
        }
    }


    // Handle by Privileges
    private void handle(HasRoomPrivilege hasRoomPrivilege) {
        Privilege privilege = hasRoomPrivilege.privilege();
        String userId = SecurityHelper.getUserId();

        if (!roomUserService.hasRoomPrivilege(userId, privilege)) {
            throw new ForbiddenException("Insufficient Privileges");
        }
    }

    private void handle(HasSystemPrivilege hasRoomPrivilege) {
        Privilege privilege = hasRoomPrivilege.privilege();
        String userId = SecurityHelper.getUserId();

        if (!userService.hasSystemPrivilege(userId, privilege)) {
            throw new ForbiddenException("Insufficient Privileges");
        }
    }


    // Handle by Roles
    private void handle(HasRoomRole hasRoomRole) {
        Role role = hasRoomRole.role();
        String userId = SecurityHelper.getUserId();

        if (!roomUserService.hasRoomRole(userId, role)) {
            throw new ForbiddenException("Insufficient Privileges");
        }
    }

    private void handle(HasSystemRole hasSystemRole) {
        Role role = hasSystemRole.role();
        String userId = SecurityHelper.getUserId();

        if (!userService.hasSystemRole(userId, role)) {
            throw new ForbiddenException("Insufficient Privileges");
        }
    }


    // Handle room events
    private void handleUpdateRoomSongs() {
        String userId = SecurityHelper.getUserId();
        Optional<RoomUser> roomUserOpt = roomUserService.getRoomUser(userId);

        if (roomUserOpt.isPresent()) {
            Long roomId = roomUserOpt.get().getRoomId();
            webSocketController.sendToRoom("songs", roomId, songService.getSongListByRoomId(roomId));
        }
    }

    private void handleUpdateRoomUsers() {
        String userId = SecurityHelper.getUserId();
        Optional<RoomUser> roomUserOpt = roomUserService.getRoomUser(userId);

        if (roomUserOpt.isPresent()) {
            Long roomId = roomUserOpt.get().getRoomId();
            webSocketController.sendToRoom("users", roomId, roomUserService.getRoomUserModelsByRoomId(roomId));
        }
    }
}
