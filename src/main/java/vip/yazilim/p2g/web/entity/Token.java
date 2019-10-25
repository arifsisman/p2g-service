package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = Constants.TABLE_PREFIX + "token")
@Data
public class Token implements Serializable {

    @Id
    private String uuid;
    private String source;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;
}
