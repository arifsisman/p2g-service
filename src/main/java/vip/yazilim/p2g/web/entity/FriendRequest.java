package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = Constants.TABLE_PREFIX + "friend_request")
@Data
public class FriendRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "user_uuid")
    private String userUuid;

    @Column(name = "friend_uuid")
    private String friendUuid;

    @Column(name = "request_status")
    private String requestStatus;

    @Column(name = "request_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime requestDate;

}
