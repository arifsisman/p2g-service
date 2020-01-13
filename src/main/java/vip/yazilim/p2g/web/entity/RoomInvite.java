package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = Constants.TABLE_PREFIX + "room_invite", uniqueConstraints = @UniqueConstraint(columnNames = {"room_uuid", "user_uuid"}))
@Data
public class RoomInvite {

    @Id
    @SequenceGenerator(name = "room_invite_id_seq", sequenceName = "room_invite_id_seq", allocationSize = 7)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_invite_id_seq")
    @Column(name = "id", unique = true, updatable = false, nullable = false, columnDefinition = "serial")
    private Long id;

    @Column(name = "room_uuid", length = 64)
    private UUID roomUuid;

    @Column(name = "user_uuid", updatable = false, nullable = false)
    private UUID userUuid;

    @Column(name = "invitation_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime invitationDate;

    @Column(name = "accepted_flag")
    private Boolean acceptedFlag;

}
