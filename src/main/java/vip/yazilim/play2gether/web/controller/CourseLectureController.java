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
import vip.yazilim.play2gether.web.entity.old.AttendanceLog;
import vip.yazilim.play2gether.web.entity.old.Course;
import vip.yazilim.play2gether.web.entity.old.Lecture;
import vip.yazilim.play2gether.web.model.form.LectureForm;
import vip.yazilim.play2gether.web.service.old.ICourseServiceI;
import vip.yazilim.play2gether.web.service.old.ILectureServiceI;
import vip.yazilim.play2gether.web.util.DBHelper;

import java.util.List;

/**
 * @author Emre Sen - 25.05.2019
 * @contact maemresen07@gmail.com
 */
@Controller
public class CourseLectureController {

    @Autowired
    private ICourseServiceI courseService;

    @Autowired
    private ILectureServiceI lectureService;


    @GetMapping("/course/{courseUuid}/lecture/all")
    public String lectures(@PathVariable String courseUuid, Model model) throws Exception {
        model.addAttribute("courseUuid", courseUuid);
        model.addAttribute("lectureList", lectureService.getLectureListByCourse(courseUuid));
        model.addAttribute("courseInfo", courseService.getCourseInfo(courseUuid));
        return "page_course_lectures";
    }


    @GetMapping("/course/{courseUuid}/lecture")
    @Secured("ROLE_MANAGER")
    public String newLecture(@PathVariable String courseUuid, LectureForm lectureForm, Model model) throws Exception {
        lectureForm.setCourseUuid(courseUuid);
        model.addAttribute("lectureForm", lectureForm);
        lectureForm.setOperation(Constants.FORM_OPERATION_INSERT);
        model.addAttribute("courseInfo", courseService.getCourseInfo(courseUuid));
        return "form_lecture";
    }

    @GetMapping("/course/{courseUuid}/lecture/{lectureUuid}")
    @Secured("ROLE_MANAGER")
    public String editLecture(@PathVariable String courseUuid, @PathVariable String lectureUuid, LectureForm lectureForm, Model model) throws Exception {
        Course course = courseService.getByUuid(courseUuid)
                .orElseThrow(() -> new Exception("Course Not Found"));

        Lecture lecture = lectureService.getByUuid(lectureUuid)
                .orElseThrow(() -> new Exception("Lecture Not Found"));

        lectureForm = new LectureForm();
        lectureForm.setCourseUuid(course.getUuid());
        lectureForm.setCourseName(course.getName());
        lectureForm.setCourseDescription(course.getDescription());

        lectureForm.setUuid(lecture.getUuid());
        lectureForm.setName(lecture.getName());
        lectureForm.setDescription(lecture.getDescription());
        lectureForm.setOperation(Constants.FORM_OPERATION_UPDATE);

        model.addAttribute("lectureForm", lectureForm);
        model.addAttribute("courseInfo", courseService.getCourseInfo(courseUuid));
        return "form_lecture";
    }

    @PostMapping("/course/lecture/save")
    @Secured("ROLE_MANAGER")
    public String saveLecture(LectureForm lectureForm, RedirectAttributes redirectAttributes) throws Exception {

        // TODO: id problem
        Lecture lecture = new Lecture();
        String uuid = lectureForm.getUuid();

        if (uuid == null || uuid.equals("")) {
            lecture.setUuid(DBHelper.getRandomUuid());
        } else {
            lecture.setUuid(uuid);
        }

        Course course = courseService.getByUuid(lectureForm.getCourseUuid())
                .orElseThrow(() -> new Exception("Course Not Found"));

        lecture.setName(lectureForm.getName());
        lecture.setDescription(lectureForm.getDescription());
        lecture.setCourse(course);

        lecture = lectureService.save(lecture)
                .orElseThrow(() -> new Exception("Lecture not saved"));

        redirectAttributes.addFlashAttribute("operation", lectureForm.getOperation());

        return "redirect:/course/" + course.getUuid() + "/lecture/" + lecture.getUuid();
    }

    @GetMapping("/course/{courseUuid}/lecture/{lectureUuid}/attendances")
    public String attendanceList(@PathVariable String courseUuid, @PathVariable String lectureUuid, Model model) throws Exception {
        Lecture lecture = lectureService.getByUuid(lectureUuid)
                .orElseThrow(() -> new Exception("Lecture Not Found"));
        List<AttendanceLog> attendanceLogList = lecture.getAttendanceLogList();
        attendanceLogList.forEach(attendanceLog -> {
            System.out.println(attendanceLog.getEnrollment());
            System.out.println(attendanceLog.getEnrollment().getStudent());
            System.out.println(attendanceLog.getEnrollment().getStudent().getSystemUser());
            System.out.println();

        });
        model.addAttribute("attendanceLogList", attendanceLogList);
        model.addAttribute("courseInfo", courseService.getCourseInfo(courseUuid));
        model.addAttribute("lectureInfo", lectureService.getLectureInfo(lectureUuid));
        return "page_course_lecture_attendances";
    }

}