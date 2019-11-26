package vip.yazilim.p2g.web.entity.relation;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = Constants.RELATION_TABLE_PREFIX + "room_invite")
@Data
public class RoomInvite {

    @Id
    private String uuid;

    @Column(name = "room_uuid")
    private String roomUuid;

    @Column(name = "user_uuid")
    private String userUuid;

    @Column(name = "invitation_date")
    private String invitationDate;

    @Column(name = "accepted_flag")
    private Boolean acceptedFlag;

}
