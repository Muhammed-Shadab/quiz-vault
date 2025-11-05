package com.minProject.root.controller;

import com.minProject.root.entity.Student;
import com.minProject.root.entity.Teacher;
import com.minProject.root.repository.TeacherRepository;
import com.minProject.root.service.TeacherService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class pageController {

    @Autowired
    private TeacherRepository teacherRepo;

    @GetMapping("/welcome")
    public String goTowelcome() {
        return "welcomePage";
    }

    @GetMapping("/choice")
    public String goToChoice(){
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

    @GetMapping("/teacherHomePage")
    public String goToteacherHomePage(HttpSession session,Model model) {
        String email = (String)session.getAttribute("email");
        if (email != null) {
            Teacher t = teacherRepo.GetByEmail(email);
            model.addAttribute("teacher",t);
            return "teacherHomePage";
        }
        return "welcomePage";
    }

    @GetMapping("/teacherProfile")
    public String goToTeacherProfile(HttpSession session,Model model) {
        String email = (String)session.getAttribute("email");
        if (email != null) {
            Teacher t = teacherRepo.GetByEmail(email);
            model.addAttribute("teacher",t);
            return "teacherProfile";
        }
        return "welcomePage";
    }
    @GetMapping("/createQuiz")
    public String goToCreateQuiz() {return "createQuiz";}



}
