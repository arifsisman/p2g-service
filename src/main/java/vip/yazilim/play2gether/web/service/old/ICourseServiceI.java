package vip.yazilim.play2gether.web.service.old;

import vip.yazilim.play2gether.web.entity.old.Course;
import vip.yazilim.play2gether.web.service.ICrudService;

import java.util.List;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
public interface ICourseServiceI extends ICrudService<Course, String> {

    public List<Course> getCourseListByManager(String managerUuid);

    public String getCourseInfo(String courseUuid) throws Exception;

}
