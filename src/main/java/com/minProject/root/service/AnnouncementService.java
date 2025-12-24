package com.minProject.root.service;

import com.minProject.root.entity.*;
import com.minProject.root.repository.AnnouncementRepository;
import com.minProject.root.repository.AnnouncementStatusRepository;
import com.minProject.root.repository.RoomsRepository;
import com.minProject.root.repository.TeacherRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.util.*;

import java.time.LocalDateTime;

@Component
public class AnnouncementService {
    @Autowired
    private TeacherRepository teacherRepo;
    @Autowired
    private RoomsRepository roomRepo;
    @Autowired
    private AnnouncementStatusRepository asRepo;
    @Autowired
    private AnnouncementRepository annRepo;

    public ResponseEntity<Map<String, Object>> createAnnouncement(
            String roomName,
            String title,
            String message,
            String teacherEmail) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Check if teacher exists
            Teacher t = teacherRepo.GetByEmail(teacherEmail);
            if (t == null) {
                response.put("success", false);
                response.put("error", "Teacher not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Create announcement
            Announcement a = new Announcement();
            a.setCreatedAt(LocalDateTime.now());
            a.setTitle(title);
            a.setMessage(message);
            a.setTeacher(t);
            a.setRoomName(roomName);
            annRepo.save(a);

            // Get students in the room
            List<Student> students = roomRepo.findStudentByRoomNameAndTeacher(
                    roomName,
                    t.getTeacherId()
            );

            // Create announcement status for each student
            for (Student s : students) {
                AnnouncementStatus as = new AnnouncementStatus();
                as.setAnnouncement(a);
                as.setStatus(false);
                as.setStudent(s);
                asRepo.save(as);
            }

            // Success response
            response.put("success", true);
            response.put("message", "Announcement sent successfully");
            response.put("studentsCount", students.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Error response
            response.put("success", false);
            response.put("error", "Failed to send announcement: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }
}