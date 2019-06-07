package vip.yazilim.play2gether.web.service.old;

import vip.yazilim.play2gether.web.entity.old.AttendanceLog;
import vip.yazilim.play2gether.web.service.CrudService;

import java.util.List;
import java.util.Optional;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
public interface IAttendanceLogService extends CrudService<AttendanceLog, String> {

    public List<AttendanceLog> getAttendanceLogByCourse(String courseUuid);

    public List<AttendanceLog> getAttendanceLogByCourseAndStudent(String courseUuid, String studentUuid) throws Exception;

    public Optional<AttendanceLog> getAttendanceLogByEnrollment(String enrollmentUuid);

    public boolean saveAttendanceLogByLectureAndEnrollment(String lectureUuid,String enrollmentUuid, boolean joined) throws Exception;

}
