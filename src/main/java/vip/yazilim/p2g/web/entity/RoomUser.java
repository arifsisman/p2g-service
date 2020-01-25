package vip.yazilim.p2g.web.entity;

import lombok.Data;
import org.joda.time.DateTime;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = Constants.TABLE_PREFIX + "room_user")
@Data
public class RoomUser implements Serializable {

    @Id
    @SequenceGenerator(name = "room_user_id_seq", sequenceName = "room_user_id_seq", allocationSize = 7)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_user_id_seq")
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "room_id", length = 64)
    private Long roomId;

    @Column(name = "user_id", unique = true, updatable = false, nullable = false)
    private String userId;

    @Column(name = "role", length = 16)
    private String role;

    @Column(name = "join_date")
    private DateTime joinDate;

    @Column(name = "active_flag")
    private Boolean activeFlag;
}
