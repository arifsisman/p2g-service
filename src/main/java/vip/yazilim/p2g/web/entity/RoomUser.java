package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = Constants.TABLE_PREFIX + "room_user")
@Data
public class RoomUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "room_id", length = 64)
    private Long roomId;

    @Column(name = "user_uuid", columnDefinition = "uuid")
    private UUID userUuid;

    @Column(name = "role_name", length = 15)
    private String roleName;

    @Column(name = "join_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime joinDate;

    @Column(name = "active_flag")
    private Boolean activeFlag;
}
