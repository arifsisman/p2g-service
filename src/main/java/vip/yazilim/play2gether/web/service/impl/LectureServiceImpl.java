package vip.yazilim.play2gether.web.service.impl;

import vip.yazilim.play2gether.web.entity.Lecture;
import vip.yazilim.play2gether.web.repository.ILectureRepo;
import vip.yazilim.play2gether.web.service.ILectureService;
import vip.yazilim.play2gether.web.service.IStudentService;
import vip.yazilim.play2gether.web.util.CollectionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
@Service
public class LectureServiceImpl implements ILectureService {

    @Autowired
    private ILectureRepo lectureRepo;

    @Autowired
    private IStudentService studentService;

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
