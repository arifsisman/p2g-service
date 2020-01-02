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
    @Column(name = "device_id")
    private String deviceId;

    private String source;

    @Column(name = "user_uuid")
    private String userUuid;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "active_flag")
    private Boolean activeFlag;

}
