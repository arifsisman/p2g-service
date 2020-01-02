package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = Constants.TABLE_PREFIX + "room")
@Data
public class Room implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false, length = 32)
    private String name;

    @Column(name = "owner_uuid", nullable = false, columnDefinition = "uuid")
    private UUID ownerUuid;

    @Column(name = "creation_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime creationDate;

    @Column(name = "private_flag", nullable = false)
    private Boolean privateFlag;

    @Column(length = 64)
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

    @Column(name = "country_code", length = 3)
    private String countryCode;

}
