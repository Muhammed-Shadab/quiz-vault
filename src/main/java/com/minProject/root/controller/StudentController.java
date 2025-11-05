package com.minProject.root.controller;


import com.minProject.root.entity.Student;
import com.minProject.root.entity.Teacher;
import com.minProject.root.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("/student")
@Controller
public class StudentController {
    @Autowired
    private StudentService studentSrc;

    @PostMapping("/add")
    public String addStudent(@ModelAttribute Student stn, Model model){
        model.addAttribute("teacherName", stn.getName());
        studentSrc.addStudent(stn);

        return "redirect:/welcome";
    }

    @PostMapping("/isStudentExists")
    public String isStudentVerified(@ModelAttribute Student st, RedirectAttributes redirectAttrs){
        if(studentSrc.isStudentVerified(st)) return "redirect:/studentHomePage";
        else{
            redirectAttrs.addFlashAttribute("error", "Invalid email or password");
            return "redirect:/studentPortal";
        }

    }
}
