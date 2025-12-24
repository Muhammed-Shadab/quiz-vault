package com.minProject.root.controller;

//import com.minProject.root.entity.Quizes;
import com.minProject.root.entity.QuizAttempt;
import com.minProject.root.entity.Quizes;
import com.minProject.root.entity.Student;
import com.minProject.root.entity.Teacher;
//import com.minProject.root.repository.QuizesRepository;
import com.minProject.root.repository.TeacherRepository;
import com.minProject.root.service.TeacherService;
import com.minProject.root.service.pageControllerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller
public class pageController {

    @Autowired
    private TeacherRepository teacherRepo;
    @Autowired
    private pageControllerService pageControllerSrc;

    @GetMapping("/welcome")
    public String goTowelcome() {
        return "welcomePage";
    }

    @GetMapping("/choice")
    public String goToChoice() {
        return "choice";
    }

    @GetMapping("/studentPortal")
    public String goToStudent(Model model) {
        model.addAttribute("Student", new Student()); // <- this is needed
        return "studentPortal";
    }

    @GetMapping("/teacherPortal")
    public String goToTeacher() {
        return "teacherPortal";
    }

    @GetMapping("/college")
    public String goTOCollege() {
        return "collegeCredentials";
    }

    public boolean getTeacherData(HttpSession session, Model model) {
        String email = (String) session.getAttribute("teacherEmail");
        if (email != null) {
            Teacher t = teacherRepo.GetByEmail(email);
            model.addAttribute("teacher", t);
            return true;
        }
        return false;
    }

    @GetMapping("/teacherHomePage")
    public String goToteacherHomePage(HttpSession session, Model model) {
        if (getTeacherData(session, model)) {
            pageControllerSrc.teacherHomePage(session, model);
            return "teacherHomePage";
        }
        return "welcomePage";
    }

    @GetMapping("/studentHomePage")
    public String goToStudentHomePage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("StudentEmail");
        if (email == null) return "welcomePage";
        pageControllerSrc.StudentHomePage(email, model);
        return "studentHomePage";
    }

    @GetMapping("/teacherProfile")
    public String goToTeacherProfile(HttpSession session, Model model) {
        if (getTeacherData(session, model)) return "teacherProfile";
        return "welcomePage";
    }

    @GetMapping("/createQuiz")
    public String goToCreateQuiz(HttpSession session, Model model) {
        if (getTeacherData(session, model)) return "createQuiz";
        return "welcomePage";
    }

    @GetMapping("/activeQuiz")
    public String goToActiveQuiz() {
        return "activeQuiz";
    }


    @GetMapping("/generateQuiz/{roomTitle}")
    public String generateQuizBYRoom(@PathVariable("roomTitle") String roomTitle, Model model) {
        model.addAttribute("roomTitle", roomTitle);
        return "createQuizFromRoom";
    }
}