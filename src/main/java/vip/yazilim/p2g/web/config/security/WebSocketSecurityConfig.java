package vip.yazilim.p2g.web.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author mustafaarifsisman - 24.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Order(300)
@Profile("dev")
@Configuration
public class WebSocketSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .headers().frameOptions().disable()
                .and().authorizeRequests()
                .antMatchers("/ws/**", "/app/**", "/topic/**")
                .permitAll();
    }

}
