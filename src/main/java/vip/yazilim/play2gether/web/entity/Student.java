package vip.yazilim.play2gether.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Emre Sen - 21.05.2019
 * @contact maemresen07@gmail.com
 */
@Entity
@Table(name = "student")
@Data
public class Student implements Serializable {

    @Id
    private String uuid;

    @Column(unique = true)
    private String studentId;

    @OneToOne
    @JoinColumn(name = "user_uuid")
    private SystemUser systemUser;

    @OneToMany(mappedBy = "student")
    @JsonIgnore
    private List<Enrollment> enrollmentList;

    @OneToMany(mappedBy = "student")
    private List<FaceEncoding> faceEncodingList;
}
