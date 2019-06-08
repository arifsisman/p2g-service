package vip.yazilim.play2gether.web.service.old;

import vip.yazilim.play2gether.web.entity.old.Student;
import vip.yazilim.play2gether.web.service.ICrudService;

import java.util.List;
import java.util.Optional;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
public interface IStudentServiceI extends ICrudService<Student, String> {

    public List<Student> getStudentList();

    public List<Student> getUnEnrolledStudentListByCourse(String courseUuid);

    public Optional<Student> getStudentBySystemUser(String userUuid);

    public Optional<Student> getStudentByStudentId(String studentId);

}
