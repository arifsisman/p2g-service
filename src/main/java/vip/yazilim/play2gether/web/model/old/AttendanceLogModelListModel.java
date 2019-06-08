package vip.yazilim.play2gether.web.model.old;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
@Data
@ToString
public class AttendanceLogModelListModel {

    private String lectureUuid;
    private List<AttendanceLogModel> attendanceLogModelList;
}
