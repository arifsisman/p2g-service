package vip.yazilim.play2gether.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vip.yazilim.play2gether.web.Constants;
import vip.yazilim.play2gether.web.entity.SystemUser;
import vip.yazilim.play2gether.web.entity.old.*;
import vip.yazilim.play2gether.web.model.form.CourseForm;
import vip.yazilim.play2gether.web.service.old.*;
import vip.yazilim.play2gether.web.util.DBHelper;
import vip.yazilim.play2gether.web.util.SecurityHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Emre Sen - 25.05.2019
 * @contact maemresen07@gmail.com
 */
@Controller
public class CourseController {

    @Autowired
    private IStudentServiceI studentService;

    @Autowired
    private IManagerServiceI managerService;

    @Autowired
    private ICourseServiceI courseService;

    @Autowired
    private IEnrollmentServiceI enrollmentService;


    @Autowired
    private ILectureServiceI lectureService;


    @Autowired
    private IAttendanceLogServiceI attendanceLogService;


    @GetMapping("/course/all")
    public String courses(Model model) throws Exception {

        List<Course> courseList = new ArrayList<>();

        SystemUser systemUser = SecurityHelper.getSystemUser();
        if (SecurityHelper.hasRole("ROLE_MANAGER")) {

            Manager manager = managerService.getManagerBySystemUser(systemUser.getUuid())
                    .orElseThrow(() -> new Exception("Manager not found"));

            courseList.addAll(courseService.getCourseListByManager(manager.getUuid()));
        } else if (SecurityHelper.hasRole("ROLE_STUDENT")) {

            Student student = studentService.getStudentBySystemUser(systemUser.getUuid())
                    .orElseThrow(() -> new Exception("Student not found"));

            courseList.addAll(enrollmentService.getEnrollmentListByStudent(student.getUuid())
                    .stream()
                    .map(Enrollment::getCourse)
                    .collect(Collectors.toList()));
        }
        model.addAttribute("courseList", courseList);
        return "page_courses";
    }

    @GetMapping("/course")
    @Secured("ROLE_MANAGER")
    public String newCourse(CourseForm courseForm) {
        courseForm.setOperation(Constants.FORM_OPERATION_INSERT);
        return "form_course";
    }

    @GetMapping("/course/{courseUuid}")
    @Secured("ROLE_MANAGER")
    public String editCourse(@PathVariable String courseUuid, Model model) throws Exception {
        Course course = courseService.getByUuid(courseUuid)
                .orElseThrow(() -> new Exception("Course Not Found"));
        CourseForm courseForm = new CourseForm();
        courseForm.setUuid(course.getUuid());
        courseForm.setName(course.getName());
        courseForm.setDescription(course.getDescription());
        courseForm.setOperation(Constants.FORM_OPERATION_UPDATE);

        model.addAttribute("courseForm", courseForm);
        return "form_course";
    }

    @PostMapping("/course/save")
    @Secured("ROLE_MANAGER")
    public String saveCourse(CourseForm courseForm, String courseUuid, RedirectAttributes redirectAttributes) throws Exception {

        SystemUser systemUser = SecurityHelper.getSystemUser();
        Manager manager = managerService.getManagerBySystemUser(systemUser.getUuid())
                .orElseThrow(() -> new Exception("Manager Not Found"));

        Course course = new Course();
        String uuid = courseForm.getUuid();
        if (uuid == null || uuid.equals("")) {
            course.setUuid(DBHelper.getRandomUuid());
        } else {
            course.setUuid(uuid);
        }
        course.setName(courseForm.getName());
        course.setDescription(courseForm.getDescription());
        course.setManager(manager);
        course = courseService.save(course)
                .orElseThrow(() -> new Exception("Course not saved"));

        redirectAttributes.addFlashAttribute("operation", courseForm.getOperation());
        return "redirect:/course/" + course.getUuid();
    }

    @GetMapping("/course/{courseUuid}/attendances")
    public String courseAttendances(@PathVariable String courseUuid, Model model) throws Exception {
        SystemUser systemUser = SecurityHelper.getSystemUser();

        List<Lecture> lectureList = new ArrayList<>();

        List<AttendanceLog> attendanceLogList = new ArrayList<>();

        if (SecurityHelper.hasRole("ROLE_MANAGER")) {

            Manager manager = managerService.getManagerBySystemUser(systemUser.getUuid())
                    .orElseThrow(() -> new Exception("Manager not found"));

            attendanceLogList.addAll(attendanceLogService.getAttendanceLogByCourse(courseUuid));
        } else if (SecurityHelper.hasRole("ROLE_STUDENT")) {

            Student student = studentService.getStudentBySystemUser(systemUser.getUuid())
                    .orElseThrow(() -> new Exception("Student not found"));
            attendanceLogList.addAll(attendanceLogService.getAttendanceLogByCourseAndStudent(courseUuid, student.getUuid()));
        }

        model.addAttribute("attendanceLogList", attendanceLogList);
        model.addAttribute("courseInfo", courseService.getCourseInfo(courseUuid));
        return "page_course_attendances";
    }
}