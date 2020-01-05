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
    @SequenceGenerator(name = "room_id_seq", sequenceName = "room_id_seq", allocationSize = 7)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_id_seq")
    @Column(name = "id", unique = true, updatable = false, nullable = false, columnDefinition = "serial")
    private Long id;

    @Column(nullable = false, length = 32)
    private String name;

    @Column(name = "owner_uuid", unique = true, updatable = false, nullable = false)
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

    @Column(name = "country_code", length = 4)
    private String countryCode;

}
