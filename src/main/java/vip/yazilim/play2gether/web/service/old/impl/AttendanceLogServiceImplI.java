package vip.yazilim.play2gether.web.service.old.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.play2gether.web.entity.old.AttendanceLog;
import vip.yazilim.play2gether.web.entity.old.Enrollment;
import vip.yazilim.play2gether.web.entity.old.Lecture;
import vip.yazilim.play2gether.web.repository.old.IAttendanceLogRepo;
import vip.yazilim.play2gether.web.service.old.IAttendanceLogServiceI;
import vip.yazilim.play2gether.web.service.old.IEnrollmentServiceI;
import vip.yazilim.play2gether.web.service.old.ILectureServiceI;
import vip.yazilim.play2gether.web.util.DBHelper;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
@Service
public class AttendanceLogServiceImplI implements IAttendanceLogServiceI {

    @Autowired
    private IAttendanceLogRepo attendanceLogRepo;

    @Autowired
    private IEnrollmentServiceI enrollmentService;

    @Autowired
    private ILectureServiceI lectureService;

    private Logger LOGGER = LoggerFactory.getLogger(AttendanceLogServiceImplI.class);

    @Override
    public List<AttendanceLog> getAttendanceLogByCourse(String courseUuid) {
        return lectureService.getLectureListByCourse(courseUuid)
                .stream()
                .flatMap(lecture -> lecture.getAttendanceLogList().stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceLog> getAttendanceLogByCourseAndStudent(String courseUuid, String studentUuid) throws Exception {
        return enrollmentService.getEnrollmentListByCourseAndStudent(courseUuid, studentUuid).stream()
                .flatMap(enrollment -> enrollment.getAttendanceLogList().stream())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AttendanceLog> getAttendanceLogByEnrollment(String enrollmentUuid) {
        return attendanceLogRepo.getAttendanceLogByEnrollment_Uuid(enrollmentUuid);
    }

    @Override
    public boolean saveAttendanceLogByLectureAndEnrollment(String lectureUuid, String enrollmentUuid, boolean joined) throws Exception {

        Lecture lecture = lectureService.getByUuid(lectureUuid)
                .orElseThrow(() -> new Exception("Lecture not Found"));


        Enrollment enrollment = enrollmentService.getByUuid(enrollmentUuid)
                .orElseThrow(() -> new Exception("Enrollment not Found"));

        AttendanceLog attendanceLog = getAttendanceLogByEnrollment(enrollmentUuid).orElseGet(AttendanceLog::new);
        String attendanceLogUuid = attendanceLog.getUuid();
        if (attendanceLogUuid == null || attendanceLogUuid.isEmpty()) {
            LOGGER.info("Creating new Uuid");
            attendanceLog.setUuid(DBHelper.getRandomUuid());
        }
        attendanceLog.setLecture(lecture);
        attendanceLog.setEnrollment(enrollment);
        attendanceLog.setAttendanceDate(new Date());
        attendanceLog.setEnrollment(enrollment);
        attendanceLog.setJoined(joined);
        attendanceLogRepo.save(attendanceLog);
        return true;
    }

    @Override
    public Optional<AttendanceLog> save(AttendanceLog item) {
        return Optional.of(attendanceLogRepo.save(item));
    }

    @Override
    public Optional<AttendanceLog> getByUuid(String uuid) {
        return attendanceLogRepo.findById(uuid);
    }

}
