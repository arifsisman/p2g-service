package vip.yazilim.p2g.web.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

/**
 * @author mustafaarifsisman - 14.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.simpDestMatchers("/ws/**", "/p2g/**").permitAll();
    }

    // Determines if a CSRF token is required for connecting. This protects against remote
    // sites from connecting to the application and being able to read/write data over the
    // connection. The default is false (the token is required).
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

}
