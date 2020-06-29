package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author mustafaarifsisman - 30.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Entity
@Table(name = Constants.TABLE_PREFIX + "user_device")
@Data
public class UserDevice {

    @Id
    @Column(name = "id", unique = true, updatable = false, nullable = false, length = 511)
    private String id;

    @Column(name = "user_id", updatable = false, nullable = false)
    private String userId;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "device_type", length = 63, nullable = false)
    private String deviceType;

    @Column(name = "active_flag", nullable = false)
    private Boolean activeFlag;

}
