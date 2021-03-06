package vip.yazilim.p2g.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vip.yazilim.p2g.web.entity.RoomUser;

/**
 * @author mustafaarifsisman - 13.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Configuration
public class RoomInfoMessageConfig {

    private RoomUser roomInfoUser = new RoomUser(true);

    @Bean
    public RoomUser getRoomInfoUser() {
        return roomInfoUser;
    }

}
