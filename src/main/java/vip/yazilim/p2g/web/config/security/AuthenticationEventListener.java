package vip.yazilim.p2g.web.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

/**
 * @author mustafaarifsisman - 20.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Configuration
public class AuthenticationEventListener {

    @EventListener
    public void authSuccessEventListener(AuthenticationSuccessEvent authorizedEvent) {
        // write custom code here for login success audit
        System.out.println("User Oauth2 login success");
        System.out.println("This is success event : " + authorizedEvent.getAuthentication().getPrincipal());

        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authorizedEvent.getAuthentication();
        OAuth2AuthenticationDetails oAuth2AuthenticationDetails = (OAuth2AuthenticationDetails) oAuth2Authentication.getDetails();
        System.out.println(oAuth2AuthenticationDetails.getTokenValue());
    }

    @EventListener
    public void authFailedEventListener(AbstractAuthenticationFailureEvent oAuth2AuthenticationFailureEvent) {
        // write custom code here login failed audit.
        System.out.println("User Oauth2 login Failed");
        System.out.println(oAuth2AuthenticationFailureEvent.getAuthentication().getPrincipal());
    }

}
