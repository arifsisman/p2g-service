package vip.yazilim.play2gether.web.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "listen_session")
@Data
public class ListenSession {

    @Id
    private String uuid;

    private String name;
    private String description;

    private String password;

    private boolean active;

    @OneToOne
    @JoinColumn(name = "owner_uuid")
    private P2GUser owner;

    @OneToMany(mappedBy = "listenSession")
    private List<Song> songList;

    @OneToMany(mappedBy = "listenSession")
    private List<P2GUser>  p2GUserList;
}
