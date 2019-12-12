package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author mustafaarifsisman - 12.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Entity
@Table(name = Constants.RELATION_TABLE_PREFIX + "privilege")
@Data
public class Privilege {

    @Id
    @Column(name = "privilege_name", nullable = false)
    private String privilegeName;

    @Column(name = "role_name", nullable = false)
    private String roleName;

}
