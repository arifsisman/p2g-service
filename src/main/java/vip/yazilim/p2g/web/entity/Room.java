package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = Constants.TABLE_PREFIX + "room")
@Data
public class Room implements Serializable {

    @Id
    private String uuid;

    private String name;
    private String description;

    private String password;
    private boolean active;

    @Column(name = "owner_uuid")
    private String ownerUuid;
}
