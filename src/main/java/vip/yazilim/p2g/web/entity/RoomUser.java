package vip.yazilim.p2g.web.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import vip.yazilim.p2g.web.constant.Constants;
import vip.yazilim.p2g.web.util.TimeHelper;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = Constants.TABLE_PREFIX + "room_user")
@Data
@NoArgsConstructor
public class RoomUser implements Serializable {

    @Id
    @SequenceGenerator(name = "room_user_id_seq", sequenceName = "room_user_id_seq", allocationSize = 7)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_user_id_seq")
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "room_id", updatable = false, nullable = false)
    private Long roomId;

    @Column(name = "user_id", unique = true, updatable = false, nullable = false)
    private String userId;

    @Column(name = "user_name", updatable = false, nullable = false)
    private String userName;

    @Column(name = "room_role", length = 31, nullable = false)
    private String roomRole;

    @Column(name = "join_date", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime joinDate;

    @Column(name = "active_flag", nullable = false)
    private Boolean activeFlag;

    public RoomUser(boolean isSystem) {
        this.setId(-1L);
        this.setRoomId(-1L);
        this.setUserId("p2g");
        this.setUserName("Info");
        this.setRoomRole("INFO");
        this.setJoinDate(TimeHelper.getLocalDateTimeNow());
        this.setActiveFlag(true);
    }
}
