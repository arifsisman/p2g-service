package vip.yazilim.play2gether.web.service.old.impl;

import vip.yazilim.play2gether.web.entity.old.Lecture;
import vip.yazilim.play2gether.web.repository.old.ILectureRepo;
import vip.yazilim.play2gether.web.service.old.ILectureServiceI;
import vip.yazilim.play2gether.web.service.old.IStudentServiceI;
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
public class LectureServiceImplI implements ILectureServiceI {

    @Autowired
    private ILectureRepo lectureRepo;

    @Autowired
    private IStudentServiceI studentService;

    @Override
    public Optional<Lecture> save(Lecture item) {
        return Optional.of(lectureRepo.save(item));
    }

    @Override
    public Optional<Lecture> getByUuid(String uuid) {
        return lectureRepo.findById(uuid);
    }

    @Override
    public List<Lecture> getLectureListByCourse(String courseUuid) {
        return CollectionHelper.iterableToList(
                lectureRepo.findByCourse_uuid(courseUuid)
        );
    }

    @Override
    public String getLectureInfo(String lectureUuid) throws Exception {
        return getByUuid(lectureUuid)
                .orElseThrow(() -> new Exception("Lecture not Found"))
                .getName();
    }
}
