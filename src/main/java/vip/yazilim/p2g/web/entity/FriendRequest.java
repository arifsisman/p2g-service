package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = Constants.TABLE_PREFIX + "friend_request")
@Data
public class FriendRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "user_uuid", columnDefinition = "uuid")
    private UUID userUuid;

    @Column(name = "friend_uuid", columnDefinition = "uuid")
    private UUID friendUuid;

    @Column(name = "request_status", length = 16)
    private String requestStatus;

    @Column(name = "request_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime requestDate;

}
