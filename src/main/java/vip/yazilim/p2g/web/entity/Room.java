package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = Constants.TABLE_PREFIX + "room")
@Data
public class Room implements Serializable {

    @Id
    private String uuid;

    @Column(nullable = false)
    private String name;

    @Column(name = "owner_uuid", nullable = false)
    private String ownerUuid;

    @Column(name = "creation_date")
    private String creationDate;

    @Column(name = "private_flag", nullable = false)
    private Boolean privateFlag;

    private String password;

    @Column(name = "max_users")
    private Integer maxUsers;

    @Column(name = "users_allowed_queue_flag")
    private Boolean usersAllowedQueueFlag;

    @Column(name = "users_allowed_control_flag")
    private Boolean usersAllowedControlFlag;

    @Column(name = "show_room_activity_flag")
    private Boolean showRoomActivityFlag;

    @Column(name = "active_flag")
    private Boolean activeFlag;

    private String chatUuid;

}
