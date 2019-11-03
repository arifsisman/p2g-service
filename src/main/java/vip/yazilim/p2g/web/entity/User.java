package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Emre Sen - 25.05.2019
 * @contact maemresen07@gmail.com
 */
@Entity
@Table(name = Constants.TABLE_PREFIX + "user")
@Data
public class User implements Serializable {

    @Id
    private String uuid;

//    odadaki userin rolu olarak degistirildi (RoomUser)
//    @Column(name = "role_uuid")
////    private String roleUuid;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "spotify_account_id")
    private String spotifyAccountId;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "online_status")
    private String onlineStatus;

    @Column(name = "image_url")
    private String imageUrl;

    private String anthem;

    @Column(name = "now_playing")
    private String nowPlaying;

}
