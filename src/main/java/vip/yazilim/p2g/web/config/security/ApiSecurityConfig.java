package vip.yazilim.p2g.web.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import static vip.yazilim.p2g.web.constant.Constants.API;

/**
 * @author mustafaarifsisman - 14.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Order(0)
@Configuration
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .antMatcher(API + "/**")
                .authorizeRequests().anyRequest()
                .authenticated().and().httpBasic()
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}

