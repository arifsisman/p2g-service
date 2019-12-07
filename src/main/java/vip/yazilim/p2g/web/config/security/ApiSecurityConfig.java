package vip.yazilim.p2g.web.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static vip.yazilim.p2g.web.constant.Constants.API_P2G;
import static vip.yazilim.p2g.web.constant.Constants.API_SPOTIFY;

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
                .antMatcher(API_SPOTIFY + "/**")
                .antMatcher(API_P2G + "/**")
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .httpBasic();
    }
}



