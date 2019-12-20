package vip.yazilim.p2g.web;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vip.yazilim.p2g.web.constant.Role;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomUserService;
import vip.yazilim.p2g.web.service.p2g.relation.ISongService;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;

import java.io.IOException;

@Component
public class DataInitializer implements CommandLineRunner {

    private Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private IRoomUserService roomUserService;

    @Autowired
    private ISongService songService;

    @Override
    public void run(String... args) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException {
        User arif = userService.createUser("arif", "arif", "0");
        User emre = userService.createUser("emre", "emre", "0");
        User mert = userService.createUser("mert", "mert", "0");

        Room testRoom = roomService.createRoom(arif.getUuid(), "Test Room1", "1");

        String roomUuid = testRoom.getUuid();
        LOGGER.info("--------> testRoomUuid: " + roomUuid);
        LOGGER.info("--------> arifUuid: " + arif.getUuid());
        LOGGER.info("--------> emreUuid: " + emre.getUuid());
        LOGGER.info("--------> mertUuid: " + mert.getUuid());


        roomUserService.joinRoom(roomUuid, emre.getUuid(), "1", Role.ROOM_USER);

        songService.addSongToRoom(roomUuid, "4VqPOruhp5EdPBeR92t6lQ", "spotify:track:4VqPOruhp5EdPBeR92t6lQ", "Uprising", 1200000,0);
        songService.addSongToRoom(roomUuid, "0c4IEciLCDdXEhhKxj4ThA", "spotify:track:0c4IEciLCDdXEhhKxj4ThA", "Madness", 1200000,1);
        songService.addSongToRoom(roomUuid, "7ouMYWpwJ422jRcDASZB7P", "spotify:track:7ouMYWpwJ422jRcDASZB7P", "Knights of Cydonia", 1200000,2);
        songService.addSongToRoom(roomUuid, "2takcwOaAZWiXQijPHIx7B", "spotify:track:2takcwOaAZWiXQijPHIx7B", "Time Is Running Out", 1200000,0);
    }
}
