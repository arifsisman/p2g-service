package vip.yazilim.p2g.web.entity.relation;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = Constants.TABLE_PREFIX + "user_token")
@Data
public class UserToken implements Serializable {

    @Id
    @Column(name = "user_uuid")
    private String userUuid;

    @Column(name = "token_uuid")
    private String tokenUuid;

}
