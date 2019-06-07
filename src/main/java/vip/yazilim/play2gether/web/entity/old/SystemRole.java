package vip.yazilim.play2gether.web.entity.old;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Emre Sen - 25.05.2019
 * @contact maemresen07@gmail.com
 */
@Entity
@Data
public class SystemRole implements Serializable {

    @Id
    private String uuid;

    private String name;

}
