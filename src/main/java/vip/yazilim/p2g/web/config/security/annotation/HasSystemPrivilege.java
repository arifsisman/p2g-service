package vip.yazilim.p2g.web.config.security.annotation;

import org.springframework.core.annotation.AliasFor;
import vip.yazilim.p2g.web.constant.Privilege;

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
@HasPrivilege(isRoom = false)
public @interface HasSystemPrivilege {
    @AliasFor(
            annotation = HasPrivilege.class
    )
    Privilege privilege();
}
