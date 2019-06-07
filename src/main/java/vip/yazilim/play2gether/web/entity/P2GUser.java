package vip.yazilim.play2gether.web.entity;

import vip.yazilim.play2gether.web.entity.old.SystemUser;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "p2g_user")
public class P2GUser {

    @Id
    private String uuid;

    private boolean online;


    @OneToOne
    @JoinColumn(name = "system_user_uuid")
    private SystemUser systemUser;

    @ManyToOne
    @JoinColumn(name = "listen_session_uuid")
    private ListenSession listenSession;

    @OneToMany(mappedBy = "p2gUser")
    private List<P2GToken> p2gTokenList;

}
