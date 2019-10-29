package vip.yazilim.p2g.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.exception.DatabaseException;
import vip.yazilim.p2g.web.service.ISongService;

@SpringBootApplication
public class Play2GetherWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(Play2GetherWebApplication.class, args);
    }

}
