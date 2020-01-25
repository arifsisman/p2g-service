package vip.yazilim.p2g.web.entity;

import lombok.Data;
import org.joda.time.LocalDateTime;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = Constants.TABLE_PREFIX + "friend_request", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "friend_id"}))
@Data
public class FriendRequest implements Serializable {

    @Id
    @SequenceGenerator(name = "friend_request_id_seq", sequenceName = "friend_request_id_seq", allocationSize = 7)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "friend_request_id_seq")
    @Column(name = "id", unique = true, updatable = false, nullable = false, columnDefinition = "serial")
    private Long id;

    @Column(name = "user_id", unique = true, updatable = false, nullable = false)
    private String userId;

    @Column(name = "friend_id", unique = true, updatable = false, nullable = false)
    private String friendId;

    @Column(name = "request_status", length = 16)
    private String requestStatus;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

}
