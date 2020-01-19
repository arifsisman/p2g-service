package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = Constants.TABLE_PREFIX + "oauth_token")
@Data
public class OAuthToken implements Serializable {

    @Id
    @Column(name = "uuid", unique = true, nullable = false)
    private String userId;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

}
