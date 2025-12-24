package com.minProject.root.controller;

import com.minProject.root.service.AnnouncementService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/ann")
public class AnnouncementController {

    @Autowired
    private AnnouncementService annSrc;

    @PostMapping("/SendAnnouncement/{roomName}")
    public ResponseEntity<Map<String,Object>> createAnnouncement(@PathVariable("roomName") String roomName,
                                                                 @RequestParam("title") String title,
                                                                 @RequestParam("message") String message,
                                                                 HttpSession session) {
        String teacherEmail = (String) session.getAttribute("teacherEmail");
        return annSrc.createAnnouncement(roomName,title,message,teacherEmail);
    }
}
