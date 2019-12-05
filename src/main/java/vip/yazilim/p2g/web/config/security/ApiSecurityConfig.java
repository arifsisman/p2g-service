package vip.yazilim.p2g.web.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author Emre Sen - 25.05.2019
 * @contact maemresen07@gmail.com
 */
@Configuration
@Order(0)
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .antMatcher("/s/**")
                .antMatcher("/p2g/**")
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .httpBasic();
    }
}



