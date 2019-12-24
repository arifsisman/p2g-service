package vip.yazilim.p2g.web.config.websocket;

import org.springframework.context.annotation.Configuration;
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
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat").withSockJS();
    }


//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/topic/", "/queue/");
//        config.setApplicationDestinationPrefixes("/app");
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/greeting");
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//
//        registry
//                .addEndpoint("/greeting")
//                .setHandshakeHandler(new DefaultHandshakeHandler() {
//
//                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
//                            WebSocketHandler wsHandler, Map attributes) throws Exception {
//
//                        if (request instanceof ServletServerHttpRequest) {
//                            ServletServerHttpRequest servletRequest
//                                    = (ServletServerHttpRequest) request;
//                            HttpSession session = servletRequest
//                                    .getServletRequest().getSession();
//                            attributes.put("sessionId", session.getId());
//                        }
//                        return true;
//                    }}).withSockJS();
//    }
}