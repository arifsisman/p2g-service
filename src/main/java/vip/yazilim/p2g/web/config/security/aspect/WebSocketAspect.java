package vip.yazilim.p2g.web.config.security.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import vip.yazilim.p2g.web.config.websocket.annotation.SendToRoom;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.exception.ForbiddenException;
import vip.yazilim.p2g.web.service.p2g.impl.relation.RoomUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.web.NotFoundException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 25.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Configuration
@Aspect
public class WebSocketAspect {

    private static final String ASPECT_PACKAGE_PATTERN = "execution(* vip.yazilim.p2g.web.controller.websocket.*.*(..))";
    private final Logger LOGGER = LoggerFactory.getLogger(SecurityAspect.class);

    @Autowired
    private RoomUserService roomUserService;

    /**
     * to be executed before invoking methods which matches given pattern before
     * executed
     *
     * @param jpoint point where aspect joins
     */
    @Before(ASPECT_PACKAGE_PATTERN)
    public void before(JoinPoint jpoint) throws ForbiddenException, DatabaseException {

        MethodSignature signature = (MethodSignature) jpoint.getSignature();
        Method method = signature.getMethod();
        for (Annotation annotation : method.getDeclaredAnnotations()) {
            if (annotation instanceof SendToRoom) {
                handle((SendToRoom) annotation);
            }
        }
    }

    private void handle(SendToRoom annotation) throws DatabaseException {
        String userUuid = SecurityHelper.getUserUuid();

        Optional<RoomUser> roomUser = roomUserService.getRoomUser(userUuid);

        if (roomUser.isPresent()) {
            //todo:...
        } else {
            String err = String.format("Room not found for userUuid[%s]", userUuid);
            throw new NotFoundException(err);
        }
    }

}
