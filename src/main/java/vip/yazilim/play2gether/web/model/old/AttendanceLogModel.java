package vip.yazilim.play2gether.web.model.old;


import lombok.Data;
import lombok.ToString;

/**
 * @author Emre Sen - 30.05.2019
 * @contact maemresen07@gmail.com
 */
@Data
@ToString
public class AttendanceLogModel {

    private String enrollmentUuid;
    private boolean joined;
}
