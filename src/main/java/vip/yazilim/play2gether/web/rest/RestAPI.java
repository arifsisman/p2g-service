package vip.yazilim.play2gether.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.play2gether.web.entity.old.Course;
import vip.yazilim.play2gether.web.entity.old.Enrollment;
import vip.yazilim.play2gether.web.entity.old.Manager;
import vip.yazilim.play2gether.web.entity.SystemUser;
import vip.yazilim.play2gether.web.model.old.AttendanceLogModel;
import vip.yazilim.play2gether.web.model.old.AttendanceLogModelListModel;
import vip.yazilim.play2gether.web.service.old.IAttendanceLogServiceI;
import vip.yazilim.play2gether.web.service.old.IEnrollmentServiceI;
import vip.yazilim.play2gether.web.service.old.IManagerServiceI;
import vip.yazilim.play2gether.web.util.SecurityHelper;

import java.util.List;

/**
 * @author Emre Sen - 25.05.2019
 * @contact maemresen07@gmail.com
 */
@RestController
@RequestMapping("/api")
public class RestAPI {

    private Logger LOGGER = LoggerFactory.getLogger(RestAPI.class);

    @Autowired
    private IEnrollmentServiceI enrollmentService;

    @Autowired
    private IManagerServiceI managerService;

    @Autowired
    private IAttendanceLogServiceI attendanceLogService;


    @GetMapping("/course/list")
    @Secured("ROLE_MANAGER")
    public List<Course> getCourseList() throws Exception {
        SystemUser systemUser = SecurityHelper.getSystemUser();
        Manager manager = managerService.getManagerBySystemUser(systemUser.getUuid())
                .orElseThrow(() -> new Exception("Manager not Found"));
        return manager.getCourseList();
    }

    @GetMapping(path = "/manager", produces = "application/json")
    @Secured("ROLE_MANAGER")
    public Manager getManager() throws Exception {
        SystemUser systemUser = SecurityHelper.getSystemUser();
        return managerService.getManagerBySystemUser(systemUser.getUuid())
                .orElseThrow(() -> new Exception("Manager not Found"));
    }

    @GetMapping("/enrollment/list/lecture/{lectureUuid}")
    @Secured("ROLE_MANAGER")
    public List<Enrollment> getEnrollmentListByLectureId(@PathVariable String lectureUuid) throws Exception {
        return enrollmentService.getEnrollmentListByLecture(lectureUuid);
    }

    @PostMapping("/attendance/take")
    @Secured("ROLE_MANAGER")
    public boolean saveFaceEncodingByStudentId(@RequestBody AttendanceLogModelListModel attendanceLogModelListModel) throws Exception {
        LOGGER.info(attendanceLogModelListModel.toString());
        String lectureUuid = attendanceLogModelListModel.getLectureUuid();
        for (AttendanceLogModel attendanceLogModel : attendanceLogModelListModel.getAttendanceLogModelList()) {
            String enrollmentUuid = attendanceLogModel.getEnrollmentUuid();
            boolean joined = attendanceLogModel.isJoined();
            attendanceLogService.saveAttendanceLogByLectureAndEnrollment(lectureUuid, enrollmentUuid, joined);
        }
        return true;
    }
}
