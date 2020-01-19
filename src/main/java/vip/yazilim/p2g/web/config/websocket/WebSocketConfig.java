package vip.yazilim.p2g.web.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import static vip.yazilim.p2g.web.constant.Constants.WEBSOCKET_THREAD_POOL_SIZE;
import static vip.yazilim.p2g.web.constant.Constants.WEBSOCKET_THREAD_POOL_SIZE_MAX;

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

        brokerRegistry.configureBrokerChannel().taskExecutor().corePoolSize(WEBSOCKET_THREAD_POOL_SIZE);
        brokerRegistry.configureBrokerChannel().taskExecutor().maxPoolSize(WEBSOCKET_THREAD_POOL_SIZE_MAX);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(WEBSOCKET_THREAD_POOL_SIZE);
        registration.taskExecutor().maxPoolSize(WEBSOCKET_THREAD_POOL_SIZE_MAX);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/p2g/room/{roomId}");
        registry.addEndpoint("/p2g/user/{userId}");
    }


}