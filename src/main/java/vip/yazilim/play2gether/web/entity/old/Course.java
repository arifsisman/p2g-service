package vip.yazilim.play2gether.web.entity.old;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Emre Sen - 21.05.2019
 * @contact maemresen07@gmail.com
 */
@Entity
@Table(name = "course")
@Data
public class Course implements Serializable {

    @Id
    private String uuid;

    private String name;
    private String description;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "manager_uuid")
    @JsonIgnore
    private Manager manager;

    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollmentList;

    @OneToMany(mappedBy = "course")
    private List<Lecture> lectureList;


    @Override
    public String toString() {
        return "Course{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
