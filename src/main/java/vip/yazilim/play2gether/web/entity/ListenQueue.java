package vip.yazilim.play2gether.web.entity;


import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "listen_queue")
@Data
public class ListenQueue {

    @Id
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "listen_session_uuid")
    private ListenSession listenSession;

    @OneToMany(mappedBy = "listenQueue")
    private List<Song> songList;
}
