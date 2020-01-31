package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = Constants.TABLE_PREFIX + "room_user")
@Data
public class RoomUser implements Serializable {

    @Id
    @SequenceGenerator(name = "room_user_id_seq", sequenceName = "room_user_id_seq", allocationSize = 7)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_user_id_seq")
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "room_id", updatable = false, nullable = false)
    private Long roomId;

    //TODO: ADD unique true, removed for test reasons
    @Column(name = "user_id", updatable = false, nullable = false)
    private String userId;

    @Column(name = "role", length = 16)
    private String role;

    @Column(name = "join_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime joinDate;

    @Column(name = "active_flag")
    private Boolean activeFlag;
}
