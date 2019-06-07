package vip.yazilim.play2gether.web.repository;

import vip.yazilim.play2gether.web.entity.old.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * @author Emre Sen - 21.05.2019
 * @contact maemresen07@gmail.com
 */
public interface IStudentRepo extends CrudRepository<Student, String> {

    Optional<Student> findBySystemUser_uuid(String uuid);

    Optional<Student> findByStudentId(String studentId);
}
