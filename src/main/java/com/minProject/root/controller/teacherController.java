package com.minProject.root.controller;


import com.minProject.root.entity.Teacher;
import com.minProject.root.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@RequestMapping("teacher")
@Controller
public class teacherController {

    @Autowired
    private TeacherService teacherSrc;

    @PostMapping("/add")
    public String addTeacher(@ModelAttribute Teacher t){
        teacherSrc.addTeacher(t);
        return "redirect:/welcome";
    }

    @PostMapping("/isTeacherExists")
    public String isTeacherVerified(@ModelAttribute Teacher t,RedirectAttributes redirectAttrs){
        if(teacherSrc.isTeacherVerified(t)) return "redirect:/teacherHomePage";
        else{
            redirectAttrs.addFlashAttribute("error", "Invalid email or password");
            return "redirect:/teacherPortal";
        }

    }
}
