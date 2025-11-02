package com.minProject.root.controller;


import com.minProject.root.entity.Student;
import com.minProject.root.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/student")
@Controller
public class StudentController {
    @Autowired
    private StudentService studentSrc;

    @PostMapping("/add")
    public String addStudent(@ModelAttribute Student stn){
        studentSrc.addStudent(stn);
        return "redirect:/welcome";
    }
}
