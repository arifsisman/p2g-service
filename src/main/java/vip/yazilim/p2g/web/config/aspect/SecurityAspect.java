package vip.yazilim.p2g.web.config.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import vip.yazilim.p2g.web.config.annotations.HasRoomRole;
import vip.yazilim.p2g.web.config.annotations.HasSystemRole;
import vip.yazilim.p2g.web.constant.Role;
import vip.yazilim.p2g.web.exception.ForbiddenException;
import vip.yazilim.p2g.web.service.p2g.impl.UserService;
import vip.yazilim.p2g.web.service.p2g.impl.relation.RoomUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author mustafaarifsisman - 16.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Configuration
@Aspect
public class SecurityAspect {

    private static final String ASPECT_PACKAGE_PATTERN = "execution(* vip.yazilim.p2g.web.controller.rest.*.*.*(..))";
    private final Logger LOGGER = LoggerFactory.getLogger(SecurityAspect.class);

    @Autowired
    private RoomUserService roomUserService;

    @Autowired
    private UserService userService;

    /**
     * to be executed before invoking methods which matches given pattern before
     * executed
     *
     * @param jpoint point where aspect joins
     */
    @Before(ASPECT_PACKAGE_PATTERN)
    public void before(JoinPoint jpoint) throws DatabaseException, InvalidArgumentException, ForbiddenException {

        MethodSignature signature = (MethodSignature) jpoint.getSignature();
        Method method = signature.getMethod();
        for (Annotation annotation : method.getDeclaredAnnotations()) {
            if (annotation instanceof HasRoomRole) {
                handle((HasRoomRole) annotation);
            } else if (annotation instanceof HasSystemRole) {
                handle((HasSystemRole) annotation);
            }
        }
    }

    private void handle(HasRoomRole hasRoomRole) throws DatabaseException, ForbiddenException {
        Role role = hasRoomRole.role();
        String userUuid = SecurityHelper.getUserUuid();

        boolean allowed = roomUserService.hasRoomRole(userUuid, role);

        if (!allowed) {
            throw new ForbiddenException("Operation not permitted.");
        }

        LOGGER.info("get roomUserService.hasRoomRole({}, {}}) if not throw forbidden", SecurityHelper.getUserUuid(), role);
    }


    private void handle(HasSystemRole hasSystemRole) throws DatabaseException, InvalidArgumentException, ForbiddenException {
        Role role = hasSystemRole.role();
        String userUuid = SecurityHelper.getUserUuid();

        boolean allowed = userService.hasRole(userUuid, role);

        if (!allowed) {
            throw new ForbiddenException("Operation not permitted.");
        }
        // TODO: get userService.hasRole(userUuid, role) if not throw forbidden
        LOGGER.info("get userService.hasRole({}, {}) if not throw forbidden", SecurityHelper.getUserUuid(), role);
    }
}
