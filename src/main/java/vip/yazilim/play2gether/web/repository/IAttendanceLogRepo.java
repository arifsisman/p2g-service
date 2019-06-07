package vip.yazilim.play2gether.web.repository;

import vip.yazilim.play2gether.web.entity.AttendanceLog;
import jdk.nashorn.internal.runtime.options.Option;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * @author Emre Sen - 21.05.2019
 * @contact maemresen07@gmail.com
 */
public interface IAttendanceLogRepo extends CrudRepository<AttendanceLog, String> {

    Optional<AttendanceLog> getAttendanceLogByEnrollment_Uuid(String enrollmentUuid);
}
