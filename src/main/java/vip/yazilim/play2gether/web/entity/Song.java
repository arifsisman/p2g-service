package vip.yazilim.play2gether.web.entity;


import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "song")
@Data
public class Song {

    @Id
    private String uuid;

    private String songId;
    private String name;
    private String artist;

    @ManyToOne
    @JoinColumn(name = "listen_session_uuid")
    private ListenSession listenSession;
}
