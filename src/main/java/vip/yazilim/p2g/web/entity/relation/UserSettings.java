package vip.yazilim.p2g.web.entity.relation;

import lombok.Data;
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

    @Column(name = "show_name")
    private boolean showName;

    @Column(name = "show_friends")
    private boolean showFriends;

    @Column(name = "show_anthem")
    private boolean showAnthem;

    @Column(name = "show_activity")
    private boolean showActivity;

    @Column(name = "show_online_status")
    private boolean showOnlineStatus;

    @Column(name = "allow_friend_requests")
    private boolean allowFriendRequests;

}
