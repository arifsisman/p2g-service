package vip.yazilim.play2gether.web.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Emre Sen - 21.05.2019
 * @contact maemresen07@gmail.com
 */
@Entity
@Table(name = "manager")
@Data
public class Manager implements Serializable {

    @Id
    private String uuid;

    @OneToOne
    @JoinColumn(name = "user_uuid")
    private SystemUser systemUser;

    @OneToMany(mappedBy = "manager", fetch = FetchType.EAGER)
    private List<Course> courseList;
}
