package vip.yazilim.play2gether.web.repository.old;

import vip.yazilim.play2gether.web.entity.old.Lecture;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Emre Sen - 21.05.2019
 * @contact maemresen07@gmail.com
 */
public interface ILectureRepo extends CrudRepository<Lecture, String> {

    Iterable<Lecture> findByCourse_uuid(String courseUuid);
}
