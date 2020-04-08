package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = Constants.TABLE_PREFIX + "room")
@Data
public class Room implements Serializable {

    @Id
    @SequenceGenerator(name = "room_id_seq", sequenceName = "room_id_seq", allocationSize = 7)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_id_seq")
    @Column(name = "id", unique = true, updatable = false, nullable = false, columnDefinition = "serial")
    private Long id;

    @Column(nullable = false, length = 127)
    private String name;

    @Column(name = "owner_id", unique = true, nullable = false)
    private String ownerId;

    @Column(name = "creation_date", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "private_flag", nullable = false)
    private Boolean privateFlag;

    @Column(length = 60)
    private String password;

    @Column(name = "max_users", nullable = false)
    private Integer maxUsers;

    @Column(name = "active_flag", nullable = false)
    private Boolean activeFlag;
}
