package vip.yazilim.p2g.web.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author mustafaarifsisman - 24.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry brokerRegistry) {
        brokerRegistry.enableSimpleBroker("/p2g/room", "/p2g/user");
        brokerRegistry.setApplicationDestinationPrefixes("/");

        brokerRegistry.configureBrokerChannel().taskExecutor().corePoolSize(1000);
        brokerRegistry.configureBrokerChannel().taskExecutor().corePoolSize(10000);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(1000);
        registration.taskExecutor().maxPoolSize(10000);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/p2g/room/{roomUuid}");
        registry.addEndpoint("/p2g/user/{userUuid}");
    }

}