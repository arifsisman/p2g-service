package vip.yazilim.play2gether.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Emre Sen - 25.05.2019
 * @contact maemresen07@gmail.com
 */
@Entity
@Data
public class SystemUser implements Serializable {

    @Id
    private String uuid;

    private String firstName;
    private String middleName;
    private String lastName;

    @Column(unique = true)
    private String email;

    private String password;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "role_uuid")
    @JsonIgnore
    private SystemRole systemRole;

}
