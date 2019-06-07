package vip.yazilim.play2gether.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vip.yazilim.play2gether.web.Constants;
import vip.yazilim.play2gether.web.entity.old.Enrollment;
import vip.yazilim.play2gether.web.model.form.EnrollmentForm;
import vip.yazilim.play2gether.web.service.old.ICourseService;
import vip.yazilim.play2gether.web.service.old.IEnrollmentService;
import vip.yazilim.play2gether.web.service.old.IStudentService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emre Sen - 25.05.2019
 * @contact maemresen07@gmail.com
 */
@Controller
public class CourseEnrollmentController {

    @Autowired
    private IEnrollmentService enrollmentService;

    @Autowired
    private ICourseService courseService;

    @Autowired
    private IStudentService studentService;


    @GetMapping("/course/{courseUuid}/enrollments")
    @Secured("ROLE_MANAGER")
    public String courseEnrollments(@PathVariable String courseUuid, Model model) throws Exception {
        List<Enrollment> enrollmentList = new ArrayList<>(enrollmentService.getEnrollmentListByCourse(courseUuid));
        model.addAttribute("enrollmentList", enrollmentList);
        model.addAttribute("courseUuid", courseUuid);
        model.addAttribute("courseInfo", courseService.getCourseInfo(courseUuid));
        return "page_course_enrollments";
    }

    @GetMapping("/course/{courseUuid}/enrollment/enroll")
    public String newEnrollment(@PathVariable String courseUuid, EnrollmentForm enrollmentForm, Model model) {
        model.addAttribute("studentList", studentService.getUnEnrolledStudentListByCourse(courseUuid));
        model.addAttribute("courseUuid", courseUuid);
        return "form_enrollment";
    }

    /* Post Requests */
    @PostMapping("/course/enrollment/leave")
    public String leaveCourseEnrollment(@RequestParam String courseUuid, @RequestParam String enrollmentUuid, RedirectAttributes redirectAttributes) throws Exception {
        enrollmentService.leaveEnrollment(enrollmentUuid);
        redirectAttributes.addFlashAttribute("operation", Constants.FORM_OPERATION_STUDENT_LEAVE);
        return "redirect:/course/" + courseUuid + "/enrollments";
    }


    @PostMapping("/course/enrollment/enroll")
    public String enrollStudentToCourse(@RequestParam String courseUuid, @RequestParam String studentUuid, RedirectAttributes redirectAttributes) throws Exception {
        enrollmentService.enrollStudentToCourse(courseUuid, studentUuid);
        redirectAttributes.addFlashAttribute("operation", Constants.FORM_OPERATION_STUDENT_ENROLL);
        return "redirect:/course/" + courseUuid + "/enrollments";
    }
}