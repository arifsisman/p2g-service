package vip.yazilim.p2g.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlayerService;
import vip.yazilim.spring.utils.exception.DatabaseException;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ISpotifyPlayerService player;

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoomService roomService;

    @Override
    public void run(String... args) {
        User arif = createUser("1","arif", "arif", "0");
        User emre = createUser("2","emre", "emre", "0");

        Room room = createRoom("Test Room", arif.getUuid());

        roomService.joinRoom(room.getUuid(), arif.getUuid());
        roomService.joinRoom(room.getUuid(), emre.getUuid());
    }

    private User createUser(String uuid, String email, String username, String password){
        User user = new User();
        user.setUuid(uuid);
        user.setEmail(email);
        user.setDisplayName(username);
        user.setPassword(password);
//        user.setDeviceId(player.getUsersAvailableDevices().get(0));

        try {
            userService.create(user);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        return user;
    }

    private Room createRoom(String name, String owner){
        Room room = new Room();

        room.setUuid("1");
        room.setName(name);
        room.setOwnerUuid(owner);
        room.setPrivateFlag(false);

        try {
            roomService.create(room);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        return room;
    }

}
