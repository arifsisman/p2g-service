package vip.yazilim.p2g.web.entity.relation;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = Constants.TABLE_PREFIX + "user")
@Data
public class RoomUser implements Serializable {

    @Id
    private String uuid;

    @Column(name = "system_user_uuid")
    private String systemUserUuid;

    @Column(name = "room_uuid")
    private String roomUuid;
}
