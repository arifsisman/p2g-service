package vip.yazilim.p2g.web.entity.relation;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = Constants.RELATION_TABLE_PREFIX + "friends")
@Data
public class UserFriends implements Serializable {

    @Id
    private String uuid;

    @Column(name = "user_uuid")
    private String userUuid;

    @Column(name = "friend_uuid")
    private String friendUuid;

}
