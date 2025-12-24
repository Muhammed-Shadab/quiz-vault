package com.minProject.root.controller;

import com.minProject.root.Task.FileTextExtractor;
import com.minProject.root.entity.Question;
import com.minProject.root.entity.Rooms;
import com.minProject.root.entity.Student;
import com.minProject.root.entity.Teacher;
import com.minProject.root.repository.RoomsRepository;
import com.minProject.root.repository.StudentRepository;
import com.minProject.root.repository.TeacherRepository;
import com.minProject.root.service.AIService;
import com.minProject.root.service.QuizesService;
import com.minProject.root.service.RoomService;
import com.minProject.root.service.TeacherService;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
//{/Rooms/genreateRoomsPage/{roomTitle}(roomTitle=${roomTitle})}
@Controller
@RequestMapping("/Rooms")
public class RoomsController {
    @Autowired
    private RoomService roomSrc;
    @Autowired
    private StudentRepository studentRepo;
    @Autowired
    private TeacherRepository teacherRepo;
    @Autowired
    private RoomsRepository roomRepo;
    private final com.minProject.root.service.AIService aisrc;
    private final FileTextExtractor extractor;
    @Autowired
    private QuizesService quizsrc;

    @Autowired
    public RoomsController(AIService aisrc, FileTextExtractor extractor) {
        this.aisrc = aisrc;
        this.extractor = extractor;
    }

    @PostMapping("/generateQuiz/{roomName}")
    public String generateQuiz(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("questionsCount") int count,
            @RequestParam("duration") int duration,
            @RequestParam("marksOfEachQuestion") int marksOfEachQuestion,
            @RequestParam("difficulty") String difficulty,
            @PathVariable("roomName") String roomName,
            @RequestParam("ExpireAt") LocalDateTime expireAt,
            @RequestParam("maxTabSwitches") int maxTabSwitches,
            @RequestParam("file") MultipartFile file,
            HttpSession session,
            Model model) throws IOException {


        List<Question> questions = quizsrc.GenerateQuiz(title,description,count,duration,marksOfEachQuestion,difficulty,roomName,expireAt,maxTabSwitches,file,session);
        model.addAttribute("questions",questions);
        return "generatedQuiz";
    }

    @PostMapping("/genreateRoomsPage/{roomTitle}")
    public String addRoomsData(@PathVariable String roomTitle, HttpSession session, Model model){
        return roomSrc.addRoomsData(roomTitle,session,model);
    }

    @PostMapping("/{roomName}/addStudent")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addStudentInRoom(
            @PathVariable String roomName,
            @RequestParam String email,
            HttpSession session) {
            return roomSrc.addStudentInRoom(roomName,email,session);
    }


    @GetMapping("/showAllRooms")
    public String showAllRooms(HttpSession session,Model model) {
        String teacherEmail = (String)session.getAttribute("teacherEmail");
        if(teacherEmail == null) return "welcome";
        roomSrc.putRoomsData(session,model);
        return "showAllRooms";

    }

}