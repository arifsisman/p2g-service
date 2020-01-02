package vip.yazilim.p2g.web.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = Constants.TABLE_PREFIX + "room_invite")
@Data
public class RoomInvite {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String uuid;

    @Column(name = "room_uuid")
    private String roomUuid;

    @Column(name = "user_uuid")
    private String userUuid;

    @Column(name = "invitation_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime invitationDate;

    @Column(name = "accepted_flag")
    private Boolean acceptedFlag;

}
