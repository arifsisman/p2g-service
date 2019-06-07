package vip.yazilim.play2gether.web.service.old;

import vip.yazilim.play2gether.web.entity.old.Enrollment;
import vip.yazilim.play2gether.web.service.CrudService;

import java.util.List;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
public interface IEnrollmentService extends CrudService<Enrollment, String> {

    public List<Enrollment> getEnrollmentListByCourse(String courseUuid);

    public List<Enrollment> getEnrollmentListByLecture(String lectureUuid) throws Exception;

    public List<Enrollment> getEnrollmentListByStudent(String studentUuid);

    public List<Enrollment> getEnrollmentListByCourseAndStudent(String courseUuid, String studentUuid);

    public boolean isStudentEnrolledToCourse(String studentUuid, String courseUuid);

    public void leaveEnrollment(String enrollmentUuid) throws Exception;

    public void enrollStudentToCourse(String courseUuid, String studentUuid) throws Exception;
}
