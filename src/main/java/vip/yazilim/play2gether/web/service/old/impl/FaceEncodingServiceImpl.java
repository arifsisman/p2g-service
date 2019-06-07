package vip.yazilim.play2gether.web.service.old.impl;

import vip.yazilim.play2gether.web.entity.old.FaceEncoding;
import vip.yazilim.play2gether.web.entity.old.Student;
import vip.yazilim.play2gether.web.repository.IFaceEncodingRepo;
import vip.yazilim.play2gether.web.service.old.IFaceEncodingService;
import vip.yazilim.play2gether.web.service.old.IStudentService;
import vip.yazilim.play2gether.web.util.DBHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
@Service
public class FaceEncodingServiceImpl implements IFaceEncodingService {

    @Autowired
    private IFaceEncodingRepo faceEncodingRepo;

    @Autowired
    private IStudentService studentService;

    @Override
    public boolean saveFaceEncodingByStudentId(String studentId, String encoding) throws Exception {
        Student student = studentService.getStudentByStudentId(studentId)
                .orElseThrow(() -> new Exception("Student not Found"));

        FaceEncoding faceEncoding = new FaceEncoding();
        faceEncoding.setUuid(DBHelper.getRandomUuid());
        faceEncoding.setEncoding(encoding);
        faceEncoding.setStudent(student);
        faceEncodingRepo.save(faceEncoding);
        return true;
    }

    @Override
    public Optional<FaceEncoding> save(FaceEncoding item) {
        return Optional.of(faceEncodingRepo.save(item));
    }

    @Override
    public Optional<FaceEncoding> getByUuid(String uuid) {
        return faceEncodingRepo.findById(uuid);
    }

}
