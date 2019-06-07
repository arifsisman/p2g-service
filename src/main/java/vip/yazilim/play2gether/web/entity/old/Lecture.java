package vip.yazilim.play2gether.web.entity.old;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Emre Sen - 25.05.2019
 * @contact maemresen07@gmail.com
 */
@Entity
@Data
public class Lecture implements Serializable {

    @Id
    private String uuid;

    private String name;
    private String description;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "course_uuid")
    @JsonIgnore
    private Course course;

    @OneToMany(mappedBy = "lecture")
    private List<AttendanceLog> attendanceLogList;
}
