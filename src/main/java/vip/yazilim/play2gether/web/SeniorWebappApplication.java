package vip.yazilim.play2gether.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import vip.yazilim.play2gether.web.entity.*;
import vip.yazilim.play2gether.web.service.*;
import vip.yazilim.play2gether.web.util.DBHelper;

import java.util.Date;

@SpringBootApplication
public class SeniorWebappApplication implements CommandLineRunner {

    private Logger LOGGER = LoggerFactory.getLogger(SeniorWebappApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SeniorWebappApplication.class, args);
    }

    @Autowired
    private ISystemUserService systemUserService;

    @Autowired
    private ISystemRoleService roleService;

    @Autowired
    private IManagerService managerService;

    @Autowired
    private IStudentService studentService;

    @Autowired
    private ICourseService courseService;

    @Autowired
    private IEnrollmentService enrollmentService;

    @Autowired
    private IAttendanceLogService attendanceLogService;

    @Autowired
    private ILectureService lectureService;

    private SystemRole createRole(String roleName) throws Exception {
        SystemRole systemRole = new SystemRole();
        systemRole.setUuid(DBHelper.getRandomUuid());
        systemRole.setName(roleName);
        return roleService.save(systemRole)
                .orElseThrow(() -> new Exception("Role Not Saved"));
    }

    private SystemUser createSystemUser(String email, String password, String firstName, String lastName, SystemRole systemRole) throws Exception {

        SystemUser systemUser = new SystemUser();
        systemUser.setUuid(DBHelper.getRandomUuid());
        systemUser.setFirstName(firstName);
        systemUser.setLastName(lastName);
        systemUser.setEmail(email);
        systemUser.setPassword(password);
        systemUser.setSystemRole(systemRole);
        return systemUserService.save(systemUser)
                .orElseThrow(() -> new Exception("User not Saved"));

    }

    private Manager createManager(String email, String password, String firstName, String lastName, SystemRole systemRole) throws Exception {
        SystemUser managerUser = createSystemUser(email, password, firstName, lastName, systemRole);
        Manager manager = new Manager();
        manager.setUuid(DBHelper.getRandomUuid());
        manager.setSystemUser(managerUser);
        return managerService.save(manager)
                .orElseThrow(() -> new Exception("Manager not Saved"));
    }

    private Student createStudent(String email, String password, String firstName, String lastName, String studentId, SystemRole systemRole) throws Exception {
        SystemUser studentUser = createSystemUser(email, password, firstName, lastName, systemRole);
        Student student = new Student();
        student.setUuid(DBHelper.getRandomUuid());
        student.setStudentId(studentId);
        student.setSystemUser(studentUser);
        return studentService.save(student)
                .orElseThrow(() -> new Exception("Student Not Saved"));
    }

    private Course createCourse(String name, String description, Manager manager) throws Exception {

        Course courseParallel = new Course();
        courseParallel.setUuid(DBHelper.getRandomUuid());
        courseParallel.setName(name);
        courseParallel.setDescription(description);
        courseParallel.setManager(manager);
        return courseService.save(courseParallel)
                .orElseThrow(() -> new Exception("Course Not Saved"));
    }

    private Enrollment createEnrollment(Course course, Student student) throws Exception {
        Enrollment enrollment = new Enrollment();
        enrollment.setUuid(DBHelper.getRandomUuid());
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        return enrollmentService.save(enrollment)
                .orElseThrow(() -> new Exception("Enrollment Not Saved"));
    }

    private Lecture createLecture(String name, String description, Course course) throws Exception {
        Lecture lecture = new Lecture();
        lecture.setUuid(DBHelper.getRandomUuid());
        lecture.setName(name);
        lecture.setCourse(course);
        return lectureService.save(lecture)
                .orElseThrow(() -> new Exception("Lecture Not Saved"));
    }

    private AttendanceLog createAttendanceLog(Lecture lecture, Enrollment enrollment) throws Exception {
        AttendanceLog attendanceLog = new AttendanceLog();
        attendanceLog.setUuid(DBHelper.getRandomUuid());
        attendanceLog.setAttendanceDate(new Date());
        attendanceLog.setLecture(lecture);
        attendanceLog.setEnrollment(enrollment);
        return attendanceLogService.save(attendanceLog)
                .orElseThrow(() -> new Exception("Attendance Log Not Saved"));
    }

    @Override
    public void run(String... args) throws Exception {

        /* Initialize Users */
        SystemRole managerRole = createRole("ROLE_MANAGER");
        SystemRole studentRole = createRole("ROLE_STUDENT");

        Manager managerTaner = createManager("admin", "0", "All The One", "Above", managerRole);


        Student studentEmre = createStudent("emre", "0", "Emre", "Sen", "emreid", studentRole);

        // Professors:
        Student profTaner = createStudent("taner", "0", "Taner", "Danisman", "tanerid", studentRole);
        Student profMelih = createStudent("melih", "0", "Melih", "Günay", "melihid", studentRole);
        Student profUmit = createStudent("umit", "0", "Ümit Deniz", "Uluşar", "umitid", studentRole);
        Student profEvgin = createStudent("evgin", "0", "Evgin", "Göçeri", "evginid", studentRole);
        Student profMurat = createStudent("murat", "0", "Murat", "Ak", "muratid", studentRole);
        Student profBerkay = createStudent("berkay", "0", "Mustafa Berkay", "Yılmaz", "berkayid", studentRole);
        Student profGokhan = createStudent("gokhan", "0", "Hüseyin Gökhan", "Akçay", "gokhanid", studentRole);
        Student profJoseph = createStudent("joseph", "0", "Joseph William", "Ledet", "josphid", studentRole);

        /* Parallel Computing Course */
        Course parallel = createCourse("Parallel Computing", "Parallel Computing Course", managerTaner);
        Course os = createCourse("Operating Systems", "Operating Systems Course", managerTaner);

        /* Senior Design */
        Course senior = createCourse("Senior Design Project", "Senior Design Project Sample Course", managerTaner);

        // enrollments
        createEnrollment(senior, studentEmre);
        createEnrollment(senior, profTaner);
        createEnrollment(senior, profMelih);
        createEnrollment(senior, profUmit);
        createEnrollment(senior, profEvgin);
        createEnrollment(senior, profMurat);
        createEnrollment(senior, profBerkay);
        createEnrollment(senior, profGokhan);
        createEnrollment(senior, profJoseph);

        // lectures
        createLecture("Parallel - Week 1", "desc", os);
        createLecture("Parallel - Week 2", "desc", os);
        createLecture("Senior Demo", "Demo class for Senior Design Project", senior);
    }
}
