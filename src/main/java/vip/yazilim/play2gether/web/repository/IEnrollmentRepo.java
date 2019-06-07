package vip.yazilim.play2gether.web.repository;

import vip.yazilim.play2gether.web.entity.old.Enrollment;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Emre Sen - 25.05.2019
 * @contact maemresen07@gmail.com
 */
public interface IEnrollmentRepo extends CrudRepository<Enrollment, String> {

    public Iterable<Enrollment> findByCourse_uuidAndRemovedFlag(String courseUuid, boolean removedFlag);

    public Iterable<Enrollment> findByStudent_uuidAndRemovedFlag(String studentUuid, boolean removedFlag);

    public Iterable<Enrollment> findByCourse_uuidAndStudent_uuidAndRemovedFlag(String courseUuid, String studentUuid, boolean removedFlag);

}
