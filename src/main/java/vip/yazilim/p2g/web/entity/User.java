package vip.yazilim.p2g.web.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Entity
@Table(name = Constants.TABLE_PREFIX + "user")
@Data
public class User implements Serializable, UserDetails {

    @Id
    private String uuid;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String role;

    @Column(name = "online_status")
    private String onlineStatus;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "image_url")
    private String imageUrl;

    private String anthem;

    @Column(name = "now_playing")
    private String nowPlaying;

    // Spotify
    @Column(name = "spotify_product_type")
    private String spotifyProductType;

    @Column(name = "spotify_account_id")
    private String spotifyAccountId;

    // Settings
    @Column(name = "show_activity_flag")
    private Boolean showActivityFlag;

    @Column(name = "show_friends_flag")
    private Boolean showFriendsFlag;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        return authorityList;
    }

    // User details methods
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return displayName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
