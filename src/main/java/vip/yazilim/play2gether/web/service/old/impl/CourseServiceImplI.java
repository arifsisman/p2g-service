package vip.yazilim.play2gether.web.service.old.impl;

import vip.yazilim.play2gether.web.entity.old.Course;
import vip.yazilim.play2gether.web.repository.old.ICourseRepo;
import vip.yazilim.play2gether.web.service.old.ICourseServiceI;
import vip.yazilim.play2gether.web.util.CollectionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
@Service
public class CourseServiceImplI implements ICourseServiceI {

    @Autowired
    private ICourseRepo courseRepo;

    @Override
    public List<Course> getCourseListByManager(String managerUuid) {
        return CollectionHelper.iterableToList(courseRepo.findByManager_uuid(managerUuid));
    }

    @Override
    public String getCourseInfo(String courseUuid) throws Exception {

        return getByUuid(courseUuid)
                .orElseThrow(() -> new Exception("Course not Found"))
                .getName();
    }

    @Override
    public Optional<Course> save(Course item) {
        return Optional.of(courseRepo.save(item));
    }

    @Override
    public Optional<Course> getByUuid(String uuid) {
        return courseRepo.findById(uuid);
    }

}
