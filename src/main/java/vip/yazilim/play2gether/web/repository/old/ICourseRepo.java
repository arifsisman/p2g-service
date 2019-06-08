package vip.yazilim.play2gether.web.repository.old;

import vip.yazilim.play2gether.web.entity.old.Course;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Emre Sen - 21.05.2019
 * @contact maemresen07@gmail.com
 */
public interface ICourseRepo extends CrudRepository<Course, String> {

    public Iterable<Course> findByManager_uuid(String uuid);
}
