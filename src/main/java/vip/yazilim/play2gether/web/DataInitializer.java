package vip.yazilim.play2gether.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import vip.yazilim.play2gether.web.entity.ListenSession;
import vip.yazilim.play2gether.web.entity.P2GUser;
import vip.yazilim.play2gether.web.entity.Song;
import vip.yazilim.play2gether.web.entity.SystemRole;
import vip.yazilim.play2gether.web.entity.SystemUser;
import vip.yazilim.play2gether.web.service.*;
import vip.yazilim.play2gether.web.util.DBHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private IListenSessionService listenSessionService;

    @Autowired
    private IP2GTokenService p2gTokenService;

    @Autowired
    private IP2GUserService p2gUserService;

    @Autowired
    private ISongService songService;

    @Autowired
    private ISystemRoleService systemRoleService;

    @Autowired
    private ISystemUserService systemUserService;

    private SystemRole createRole(String roleName) throws Exception {

        SystemRole systemRole = new SystemRole();
        systemRole.setUuid(DBHelper.getRandomUuid());
        systemRole.setName(roleName);
        return systemRoleService.save(systemRole).orElseThrow(() -> new Exception("Role Not Saved"));
    }

    private SystemUser createSystemUser(String email, String password, String firstName, String lastName,
            SystemRole systemRole) throws Exception {

        SystemUser systemUser = new SystemUser();
        systemUser.setUuid(DBHelper.getRandomUuid());
        systemUser.setFirstName(firstName);
        systemUser.setLastName(lastName);
        systemUser.setEmail(email);
        systemUser.setPassword(password);
        systemUser.setSystemRole(systemRole);
        return systemUserService.save(systemUser).orElseThrow(() -> new Exception("User not Saved"));
    }

    private P2GUser createP2GUser(String email, String password, String firstName, String lastName,
            SystemRole systemRole, boolean online) throws Exception {

        SystemUser systemUser = createSystemUser(email, password, firstName, lastName, systemRole);
        P2GUser p2gUser = new P2GUser();
        p2gUser.setUuid(DBHelper.getRandomUuid());
        p2gUser.setOnline(online);
        p2gUser.setSystemUser(systemUser);
        p2gUser.setP2gTokenList(Collections.EMPTY_LIST);
        return p2gUserService.save(p2gUser).orElseThrow(() -> new Exception("P2GUser not Saved"));
    }

    private Song createSong(String songId, String name, String artist) throws Exception {
        Song song = new Song();
        song.setUuid(DBHelper.getRandomUuid());
        song.setSongId(songId);
        song.setName(name);
        song.setArtist(artist);
        return songService.save(song).orElseThrow(() -> new Exception("Song not Saved"));
    }

    private ListenSession createListenSession(String name, String description, String password, boolean active, P2GUser owner, List<P2GUser> p2GUserList, List<Song> songList) throws Exception {
        ListenSession listenSession = new ListenSession();
        String uuid = DBHelper.getRandomUuid();
        listenSession.setUuid(uuid);
        listenSession.setName(name);
        listenSession.setDescription(description);
        listenSession.setPassword(password);
        listenSession.setActive(active);
        listenSession.setOwner(owner);
        listenSession.setP2GUserList(p2GUserList);
        listenSession.setSongList(songList);

        for (Song s: songList) {
            s.setListenSession(uuid);
        }

        for (P2GUser u: p2GUserList) {
            u.setListenSession(uuid);
        }

        return listenSessionService.save(listenSession)
                .orElseThrow(() -> new Exception("Listen Session Not Saved"));
    }

    @Override
    public void run(String... args) throws Exception {

        /* Initialize Roles */
        LOGGER.info("Initialize Roles");
        SystemRole adminRole = createRole("ROLE_ADMIN");
        SystemRole userRole = createRole("ROLE_USER");

        /* Initialize Users */
        LOGGER.info("Initialize Users");
        P2GUser emre = createP2GUser("emre", "0", "Emre", "Sen", userRole, true);
        P2GUser mustafa = createP2GUser("mustafa", "0", "Mustafa", "Sisman", adminRole, true);

        /* Initialize User List */
        LOGGER.info("Initialize User List");
        List<P2GUser> p2GUserList = new ArrayList<>();
        p2GUserList.add(emre);
        p2GUserList.add(mustafa);

        /* Initialize Songs */
        LOGGER.info("Initialize Songs");
        Song song1 = createSong("1", "Biraz Ayrilik", "Gokhan Turkmen");
        Song song2 = createSong("2", "Kandırdım", "Kenan Doğulu");
        Song song3 = createSong("3", "Fırtınalar", "Pamela Spence");
        Song song4 = createSong("4", "Everybody's Fool", "Evanescence");
        Song song5 = createSong("5", "bury a friend", "Billie Ellish");

        /* Initialize Songs List */
        LOGGER.info("Initialize Song List");
        List<Song> songList = new ArrayList<>();
        songList.add(song1);
        songList.add(song2);

        /* Initialize Listen Sessions */
        LOGGER.info("Initialize Listen Sessions");
        ListenSession listenSession = createListenSession("Test Room", "Test Description", "0", true, mustafa, p2GUserList, songList);
    }
}
