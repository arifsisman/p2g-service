package vip.yazilim.p2g.web.entity;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

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
    private Timestamp creationDate;

    private String name;
    private String description;

    @Column(name = "private", nullable = false)
    private boolean isPrivate;
    private String password;

    @Column(name = "max_users")
    private int maxUsers;

    @Column(name = "active")
    private boolean isActive;

    @Column(name = "is_everyone_allowed_queue")
    private boolean isEveryoneAllowedQueue;

    @Column(name = "is_everyone_allowed_control")
    private boolean isEveryoneAllowedControl;

    @Column(name = "show_room_activity")
    private boolean showRoomActivity;

}
