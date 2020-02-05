package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = Constants.TABLE_PREFIX + "room_invite", uniqueConstraints = @UniqueConstraint(columnNames = {"room_id", "user_id"}))
@Data
public class RoomInvite {

    @Id
    @SequenceGenerator(name = "room_invite_id_seq", sequenceName = "room_invite_id_seq", allocationSize = 7)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_invite_id_seq")
    @Column(name = "id", unique = true, updatable = false, nullable = false, columnDefinition = "serial")
    private Long id;

    @Column(name = "room_id", length = 128)
    private Long roomId;

    @Column(name = "inviter_id", updatable = false, nullable = false)
    private String inviterId;

    @Column(name = "user_id", updatable = false, nullable = false)
    private String userId;

    @Column(name = "invitation_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime invitationDate;

}
