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

    private ListenSession createListenSession(String name, String description, boolean active, P2GUser owner,
            List<Song> songList, List<P2GUser> p2GUserList) throws Exception {

        ListenSession listenSession = new ListenSession();
        listenSession.setUuid(DBHelper.getRandomUuid());
        listenSession.setName(name);
        listenSession.setDescription(description);
        listenSession.setActive(active);
        listenSession.setOwner(owner);
        listenSession.setSongList(songList);
        listenSession.setP2GUserList(p2GUserList);

        return listenSessionService.save(listenSession).orElseThrow(() -> new Exception("Listen Session not Saved"));
    }

    @Override
    public void run(String... args) throws Exception {

        /* Initialize Users */
        LOGGER.info("Initialize Roles");
        SystemRole roleUser = createRole("ROLE_USER");

        // Users:
        LOGGER.info("Initialize Users");
        P2GUser emre = createP2GUser("emre", "0", "Emre", "Sen", roleUser, true);
        P2GUser mustafa = createP2GUser("mustafa", "0", "Mustafa", "Sisman", roleUser, true);

        P2GUser profTaner = createP2GUser("taner", "0", "Taner", "Danisman", roleUser, false);
        P2GUser profMelih = createP2GUser("melih", "0", "Melih", "Günay", roleUser, false);
        P2GUser profUmit = createP2GUser("umit", "0", "Ümit Deniz", "Uluşar", roleUser, false);
        P2GUser profEvgin = createP2GUser("evgin", "0", "Evgin", "Göçeri", roleUser, false);
        P2GUser profMurat = createP2GUser("murat", "0", "Murat", "Ak", roleUser, false);
        P2GUser profBerkay = createP2GUser("berkay", "0", "Mustafa Berkay", "Yılmaz", roleUser, false);
        P2GUser profGokhan = createP2GUser("gokhan", "0", "Hüseyin Gökhan", "Akçay", roleUser, false);
        P2GUser profJoseph = createP2GUser("joseph", "0", "Joseph William", "Ledet", roleUser, false);

        LOGGER.info("Initialize Songs");
        Song song1 = createSong("1", "Biraz Ayrilik", "Gokhan Turkmen");
        Song song2 = createSong("2", "Kandırdım", "Kenan Doğulu");
        Song song3 = createSong("3", "Fırtınalar", "Pamela Spence");

        LOGGER.info("Initialize ListenSessions");
        ListenSession listenSession = createListenSession("Session 1", "Desc", true, emre,
                Arrays.asList(song1, song2, song3), Arrays.asList(emre, mustafa));
    }
}
