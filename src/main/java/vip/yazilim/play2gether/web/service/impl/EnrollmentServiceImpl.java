package vip.yazilim.play2gether.web.service.impl;

import vip.yazilim.play2gether.web.entity.Course;
import vip.yazilim.play2gether.web.entity.Enrollment;
import vip.yazilim.play2gether.web.entity.Lecture;
import vip.yazilim.play2gether.web.entity.Student;
import vip.yazilim.play2gether.web.repository.IEnrollmentRepo;
import vip.yazilim.play2gether.web.service.ICourseService;
import vip.yazilim.play2gether.web.service.IEnrollmentService;
import vip.yazilim.play2gether.web.service.ILectureService;
import vip.yazilim.play2gether.web.service.IStudentService;
import vip.yazilim.play2gether.web.util.CollectionHelper;
import vip.yazilim.play2gether.web.util.DBHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
@Service
public class EnrollmentServiceImpl implements IEnrollmentService {

    @Autowired
    private IEnrollmentRepo enrollmentRepo;

    @Autowired
    private ICourseService courseService;

    @Autowired
    private IStudentService studentService;

    @Autowired
    private ILectureService lectureService;

    @Override
    public List<Enrollment> getEnrollmentListByCourse(String courseUuid) {
        return CollectionHelper.iterableToList(
                enrollmentRepo.findByCourse_uuidAndRemovedFlag(courseUuid, false)
        );
    }

    @Override
    public List<Enrollment> getEnrollmentListByLecture(String lectureUuid) throws Exception {

        Lecture lecture = lectureService.getByUuid(lectureUuid)
                .orElseThrow(() -> new Exception("Lecture not Found"));
        String courseUuid = lecture.getCourse().getUuid();
        return CollectionHelper.iterableToList(
                enrollmentRepo.findByCourse_uuidAndRemovedFlag(courseUuid, false)
        );
    }

    @Override
    public List<Enrollment> getEnrollmentListByStudent(String studentUuid) {
        return CollectionHelper.iterableToList(
                enrollmentRepo.findByStudent_uuidAndRemovedFlag(studentUuid, false)
        );
    }

    @Override
    public List<Enrollment> getEnrollmentListByCourseAndStudent(String courseUuid, String studentUuid) {
        return CollectionHelper.iterableToList(
                enrollmentRepo.findByCourse_uuidAndStudent_uuidAndRemovedFlag(courseUuid, studentUuid, false)
        );
    }

    @Override
    public boolean isStudentEnrolledToCourse(String studentUuid, String courseUuid) {
        List<Enrollment> enrollmentList = getEnrollmentListByCourseAndStudent(courseUuid, studentUuid);
        return !enrollmentList.isEmpty();
    }

    @Override
    public void leaveEnrollment(String enrollmentUuid) throws Exception {
        Enrollment enrollment = getByUuid(enrollmentUuid).
                orElseThrow(() -> new Exception("Enrollment Not Found with uuid:" + enrollmentUuid));
        enrollment.setRemovedFlag(true);
        save(enrollment);
    }

    @Override
    public void enrollStudentToCourse(String courseUuid, String studentUuid) throws Exception {

        Course course = courseService.getByUuid(courseUuid)
                .orElseThrow(() -> new Exception("Course not Found Uuid: " + courseUuid));

        Student student = studentService.getByUuid(studentUuid)
                .orElseThrow(() -> new Exception("Student not Found Uuid: " + studentUuid));
        // TODO: should check if already enrolled

        Enrollment enrollment = new Enrollment();
        enrollment.setUuid(DBHelper.getRandomUuid());
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollmentRepo.save(enrollment);
    }

    @Override
    public Optional<Enrollment> save(Enrollment item) {
        return Optional.of(enrollmentRepo.save(item));
    }

    @Override
    public Optional<Enrollment> getByUuid(String uuid) {
        return enrollmentRepo.findById(uuid);
    }

}
