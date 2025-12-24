package com.minProject.root.controller;


import com.minProject.root.entity.Student;
import com.minProject.root.entity.Teacher;
import com.minProject.root.repository.StudentRepository;
import com.minProject.root.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@RequestMapping("/student")
@Controller
public class StudentController {
    @Autowired
    private StudentService studentSrc;

    @Autowired
    private StudentRepository studentRepo;

    @PostMapping("/add")
    public String addStudent(@ModelAttribute Student stn, Model model,HttpSession session){
        model.addAttribute("studentName", stn.getName());
        studentSrc.addStudent(stn);
        session.setAttribute("StudentEmail",stn.getEmail());
        return "redirect:/studentHomePage";
    }

    @PostMapping("/isStudentExists")
    public String isStudentVerified(@ModelAttribute Student st, RedirectAttributes redirectAttrs, HttpSession session){
        if(studentSrc.isStudentVerified(st, session, redirectAttrs)){  // Pass redirectAttrs instead of model
            session.setAttribute("StudentEmail", st.getEmail());
            return "redirect:/studentHomePage";
        }
        else{
            redirectAttrs.addFlashAttribute("error", "Invalid email or password");
            return "redirect:/studentPortal";
        }
    }
    @GetMapping("/AllAnnouncement")
    public String AllAnnouncement(HttpSession session,Model model) {
        String studentEmail = (String) session.getAttribute("StudentEmail");
        if(studentEmail == null) return "welcome";
        studentSrc.findAllAnnouncements(studentEmail,model);
        return "announcementPage";
    }

    @GetMapping("/getAttemptQuizData")
    public String addAllActiveQuizes(HttpSession session,Model model) {
        String email = (String) session.getAttribute("StudentEmail");
        if(email == null) return "welcomePage";
        studentSrc.addAllActiveQuizes(email,model);
        return "incompleteQuizes";
    }

    @GetMapping("/quizResults")
    public String addAttemptedQuizData(HttpSession session,Model model) {
        String email = (String) session.getAttribute("StudentEmail");
        if(email == null) return "welcomePage";
        studentSrc.addAttempedQuizData(email,model);
        return "allAttemptedQuizes";
    }

    @PostMapping("/setActiveTime")
    @ResponseBody
    public void setActiveTime(HttpSession session,@RequestBody Map<String, Object> payload) {
        studentSrc.updateActiveTime(session,payload);
    }

    @GetMapping("/check")
    public String checkInactiveUsers(HttpSession session) {
        String studentEmail = (String)session.getAttribute("StudentEmail");
        LocalDateTime lastHeartBeat = studentRepo.getLastHeartBeat(studentEmail);

        long time = Duration.between(lastHeartBeat,LocalDateTime.now()).toMillis();
        System.out.println(time);
        if(time > 10000){
            System.out.println("Inside");
            return "userIsInactive";
        }
        return "user is Active";
    }
}
