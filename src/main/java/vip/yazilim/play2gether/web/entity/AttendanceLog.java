package vip.yazilim.play2gether.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Emre Sen - 21.05.2019
 * @contact maemresen07@gmail.com
 */
@Entity
@Table(name = "attendance_log")
@Data
public class AttendanceLog implements Serializable {

    @Id
    private String uuid;
    private boolean joined;

    private Date attendanceDate;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "lecture_uuid")
    @JsonIgnore
    private Lecture lecture;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "enrollment_uuid", unique = true)
    private Enrollment enrollment;
}
