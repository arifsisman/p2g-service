package vip.yazilim.p2g.web.entity.relation;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = Constants.RELATION_TABLE_PREFIX + "room_user")
@Data
public class RoomUser implements Serializable {

    @Id
    private String uuid;

    @Column(name = "room_uuid")
    private String roomUuid;

    @Column(name = "user_uuid")
    private String userUuid;

    @Column(name = "role_uuid")
    private String roleUuid;

    @Column(name = "invitation_date")
    private LocalDateTime invitationDate;

    @Column(name = "accepted_flag")
    private Boolean acceptedFlag;

    @Column(name = "active_flag")
    private Boolean activeFlag;


}
