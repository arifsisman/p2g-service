package vip.yazilim.p2g.web.entity.relation;

import lombok.Data;
import org.springframework.core.annotation.Order;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = Constants.RELATION_TABLE_PREFIX + "user_settings")
@Data
public class UserSettings implements Serializable {

    @Id
    private String uuid;

    @Column(name = "user_uuid")
    private String userUuid;

    @Column(name = "show_name_flag")
    private Boolean showNameFlag;

    @Column(name = "show_friends_flag")
    private Boolean showFriendsFlag;

    @Column(name = "show_anthem_flag")
    private Boolean showAnthemFlag;

    @Column(name = "show_activity_flag")
    private Boolean showActivityFlag;

    @Column(name = "show_online_status_flag")
    private Boolean showOnlineStatusFlag;

    @Column(name = "allow_friend_requests_flag")
    private Boolean allowFriendRequestsFlag;

}
