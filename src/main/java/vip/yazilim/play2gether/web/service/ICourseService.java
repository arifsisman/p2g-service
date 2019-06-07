package vip.yazilim.play2gether.web.service;

import vip.yazilim.play2gether.web.entity.Course;

import java.util.List;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
public interface ICourseService extends CrudService<Course, String> {

    public List<Course> getCourseListByManager(String managerUuid);

    public String getCourseInfo(String courseUuid) throws Exception;

}
