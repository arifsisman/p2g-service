package vip.yazilim.play2gether.web.service.old.impl;

import vip.yazilim.play2gether.web.entity.old.Student;
import vip.yazilim.play2gether.web.repository.old.IStudentRepo;
import vip.yazilim.play2gether.web.service.old.IEnrollmentServiceI;
import vip.yazilim.play2gether.web.service.old.IStudentServiceI;
import vip.yazilim.play2gether.web.util.CollectionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
@Service
public class StudentServiceImplI implements IStudentServiceI {

    @Autowired
    private IStudentRepo studentRepo;

    @Autowired
    private IEnrollmentServiceI enrollmentService;

    @Override
    public List<Student> getStudentList() {
        return CollectionHelper.iterableToList(studentRepo.findAll());
    }

    @Override
    public List<Student> getUnEnrolledStudentListByCourse(String courseUuid) {

        List<Student> result = new ArrayList<>();
        for (Student student : getStudentList()) {
            String studentUuid = student.getUuid();
            if (enrollmentService.isStudentEnrolledToCourse(studentUuid, courseUuid)) {
                continue;
            }
            result.add(student);
        }
        return result;
    }

    @Override
    public Optional<Student> getStudentBySystemUser(String userUuid) {
        return studentRepo.findBySystemUser_uuid(userUuid);
    }

    @Override
    public Optional<Student> getStudentByStudentId(String studentId) {
        return studentRepo.findByStudentId(studentId);
    }

    @Override
    public Optional<Student> save(Student item) {
        return Optional.of(studentRepo.save(item));
    }

    @Override
    public Optional<Student> getByUuid(String uuid) {
        return studentRepo.findById(uuid);
    }

}
