package vip.yazilim.p2g.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableResourceServer
public class Play2GetherWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(Play2GetherWebApplication.class, args);
    }

}