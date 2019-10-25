package vip.yazilim.play2gether.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import vip.yazilim.play2gether.web.entity.*;
import vip.yazilim.play2gether.web.entity.old.*;
import vip.yazilim.play2gether.web.service.*;
import vip.yazilim.play2gether.web.service.old.*;
import vip.yazilim.play2gether.web.util.DBHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class Play2GetherWebApplication {

    private Logger LOGGER = LoggerFactory.getLogger(Play2GetherWebApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(Play2GetherWebApplication.class, args);
    }

//    @Autowired
//    private ISystemUserService systemUserService;
//
//    @Autowired
//    private ISystemRoleService roleService;
//
//    @Autowired
//    private IP2GUserService p2GUserService;
//
//    @Autowired
//    private IListenSessionService listenSessionService;
//
//    @Autowired
//    private ISongService songService;
//
//
//    private SystemRole createRole(String roleName) throws Exception {
//        SystemRole systemRole = new SystemRole();
//        systemRole.setUuid(DBHelper.getRandomUuid());
//        systemRole.setName(roleName);
//        return roleService.save(systemRole)
//                .orElseThrow(() -> new Exception("Role Not Saved"));
//    }
//
//    private SystemUser createSystemUser(String email, String password, String firstName, String lastName, SystemRole systemRole) throws Exception {
//        SystemUser systemUser = new SystemUser();
//        systemUser.setUuid(DBHelper.getRandomUuid());
//        systemUser.setFirstName(firstName);
//        systemUser.setLastName(lastName);
//        systemUser.setEmail(email);
//
//        systemUser.setPassword(password);
//        systemUser.setSystemRole(systemRole);
//        return systemUserService.save(systemUser)
//                .orElseThrow(() -> new Exception("User not Saved"));
//    }
//
//    private P2GUser createP2GUser(String email, String password, String firstName, String lastName, SystemRole systemRole, boolean online, ListenSession listenSession, List<P2GToken> p2gTokenList) throws Exception {
//        SystemUser sysUser = createSystemUser(email, password, firstName, lastName, systemRole);
//        P2GUser user = new P2GUser();
//        user.setUuid(DBHelper.getRandomUuid());
//        user.setSystemUser(sysUser);
//        user.setOnline(online);
//        user.setP2gTokenList(p2gTokenList);
//        user.setListenSession(listenSession);
//        return p2GUserService.save(user)
//                .orElseThrow(() -> new Exception("P2G User Not Saved"));
//    }
//
//    private Song createSong(String songId, String name, String artist, ListenSession listenSession) throws Exception {
//        Song song = new Song();
//        song.setUuid(DBHelper.getRandomUuid());
//        song.setSongId(songId);
//        song.setName(name);
//        song.setArtist(artist);
//        song.setListenSession(listenSession);
//        return songService.save(song)
//                .orElseThrow(() -> new Exception("Song Not Saved"));
//    }
//
//    private ListenSession createListenSession(String name, String description, String password, boolean active, P2GUser owner, List<P2GUser> p2GUserList, List<Song> songList) throws Exception {
//        ListenSession listenSession = new ListenSession();
//        listenSession.setName(name);
//        listenSession.setDescription(description);
//        listenSession.setPassword(password);
//        listenSession.setActive(active);
//        listenSession.setOwner(owner);
//        listenSession.setP2GUserList(p2GUserList);
//        listenSession.setSongList(songList);
//
//        for (Song s: songList) {
//            s.setListenSession(listenSession);
//        }
//
//        for (P2GUser u: p2GUserList) {
//            u.setListenSession(listenSession);
//        }
//
//        return listenSessionService.save(listenSession)
//                .orElseThrow(() -> new Exception("Listen Session Not Saved"));
//    }


    public void run(String... args) throws Exception {

//        /* Initialize Roles */
//        SystemRole adminRole = createRole("ROLE_ADMIN");
//        SystemRole userRole = createRole("ROLE_USER");
//
//        /* Initialize P2G User */
//        P2GUser user1 = createP2GUser("user1", "0", "Test", "User 1", userRole, true, null, null);
//        P2GUser user2 = createP2GUser("user2", "0", "Test", "User 2", userRole, true, null, null);
//
//        /* Initialize P2G User List */
//        List<P2GUser> p2GUserList = new ArrayList<>();
//        p2GUserList.add(user1);
//        p2GUserList.add(user2);
//
//        /* Initialize Songs */
//        Song song1 = createSong("1234", "Everybody's Fool", "Evanescence", null);
//        Song song2 = createSong("5678", "bury a friend", "Billie Ellish", null);
//
//        /* Initialize Song List */
//        List<Song> songList = new ArrayList<>();
//        songList.add(song1);
//        songList.add(song2);
//
//        /* Initialize Listen Session */
//        ListenSession listenSession = createListenSession("Test Room", "Test Description", "0", true, user1, p2GUserList, songList);
    }
}
