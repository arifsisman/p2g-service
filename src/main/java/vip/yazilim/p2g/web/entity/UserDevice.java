package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * @author mustafaarifsisman - 30.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Entity
@Table(name = Constants.TABLE_PREFIX + "user_device")
@Data
public class UserDevice {

    @Id
    @Column(name = "id", unique = true, updatable = false, nullable = false, length = 64)
    private String id;

    @Column(name = "platform", updatable = false, nullable = false, length = 16)
    private String platform;

    @Column(name = "uuid", unique = true, updatable = false, nullable = false)
    private UUID userUuid;

    @Column(name = "device_name", length = 64)
    private String deviceName;

    @Column(name = "device_type", length = 16)
    private String deviceType;

    @Column(name = "active_flag")
    private Boolean activeFlag;

}
