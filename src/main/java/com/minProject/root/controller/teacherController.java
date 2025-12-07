package com.minProject.root.controller;


import com.minProject.root.entity.Teacher;
import com.minProject.root.service.TeacherService;
import jakarta.servlet.http.HttpSession;
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
    public String addTeacher(@ModelAttribute Teacher t, RedirectAttributes redirectAttrs, HttpSession session){
        if(teacherSrc.addTeacher(t)) {
            session.setAttribute("teacherEmail",t.getEmail());
            return "redirect:/teacherHomePage";
        }
        else{
            redirectAttrs.addFlashAttribute("errorSignin", "Email exists!");
            return "redirect:/teacherPortal";
        }
    }

    @PostMapping("/isTeacherExists")
    public String isTeacherVerified(@ModelAttribute Teacher t,RedirectAttributes redirectAttrs,HttpSession session){
        if(teacherSrc.isTeacherVerified(t)){
            session.setAttribute("teacherEmail",t.getEmail());
            return "redirect:/teacherHomePage";
        }
        else{
            redirectAttrs.addFlashAttribute("errorLogin", "Invalid email or password");
            return "redirect:/teacherPortal";
        }

    }
}
