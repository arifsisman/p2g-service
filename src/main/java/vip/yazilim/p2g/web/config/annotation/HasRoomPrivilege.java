package vip.yazilim.p2g.web.config.annotation;

import org.springframework.core.annotation.AliasFor;
import vip.yazilim.p2g.web.enums.Privilege;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mustafaarifsisman - 16.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HasPrivilege(isRoom = true)
public @interface HasRoomPrivilege {
    @AliasFor(
            annotation = HasPrivilege.class
    )
    Privilege privilege();
}
