package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Emre Sen - 25.05.2019
 * @contact maemresen07@gmail.com
 */
@Entity
@Table(name = Constants.TABLE_PREFIX + "role")
@Data
public class Role implements Serializable {

    @Id
    private String uuid;
    private String name;

}
