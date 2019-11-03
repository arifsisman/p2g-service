package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = Constants.TABLE_PREFIX + "room")
@Data
public class Room implements Serializable {

    @Id
    private String uuid;

    @Column(name = "owner_uuid")
    private String ownerUuid;

    @Column(name = "queue_uuid")
    private String queueUuid;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    private String name;
    private String description;

    @Column(name = "private_flag", nullable = false)
    private Boolean privateFlag;

    private String password;

    @Column(name = "max_users")
    private int maxUsers;

    @Column(name = "active_flag")
    private Boolean activeFlag;

    @Column(name = "everyone_allowed_queue_flag")
    private Boolean everyoneAllowedQueueFlag;

    @Column(name = "everyone_allowed_control_flag")
    private Boolean everyoneAllowedControlFlag;

    @Column(name = "show_room_activity_flag")
    private Boolean showRoomActivityFlag;

}
