package vip.yazilim.play2gether.web.entity.old;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Emre Sen - 21.05.2019
 * @contact maemresen07@gmail.com
 */
@Entity
@Table(name = "face_encoding")
@Data
public class FaceEncoding implements Serializable {

    @Id
    private String uuid;

    @Column(name = "encoding", columnDefinition="TEXT")
    private String encoding;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "student_uuid")
    @JsonIgnore
    private Student student;
}
