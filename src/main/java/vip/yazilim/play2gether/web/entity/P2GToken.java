package vip.yazilim.play2gether.web.entity;


import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "p2g_token")
@Data
public class P2GToken {

    @Id
    private String uuid;

    private String source;

    private String accessToken;
    private String refreshToken;

    @ManyToOne
    @JoinColumn(name = "p2g_user_uuid")
    private P2GUser p2gUser;
}
