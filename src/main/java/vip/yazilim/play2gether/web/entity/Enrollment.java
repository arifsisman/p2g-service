package vip.yazilim.play2gether.web.entity;

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
public class Enrollment implements Serializable {

    @Id
    private String uuid;
    private boolean removedFlag;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "student_uuid")
    private Student student;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "course_uuid")
    @JsonIgnore
    private Course course;


    @OneToMany(mappedBy = "enrollment")
    @JsonIgnore
    private List<AttendanceLog> attendanceLogList;
}
