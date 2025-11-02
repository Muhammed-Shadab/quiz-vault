package com.minProject.root.controller;

import com.minProject.root.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class pageController {

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



}
