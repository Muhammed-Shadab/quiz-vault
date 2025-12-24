package com.minProject.root.service;

import com.minProject.root.entity.*;
import com.minProject.root.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class pageControllerService {
    @Autowired
    private QuizAttemptRepository QARepo;
    @Autowired
    private StudentRepository stnRepo;
    @Autowired
    private TeacherRepository teacherRepo;
    @Autowired
    private QuizesRepository quizRepo;
    @Autowired
    private RoomsRepository roomRepo;
    @Autowired
    private StudentService stnSrc;
    @Autowired
    private RoomService roomSrc;

    public void StudentHomePage(String email, Model model) {
        Student res  = stnRepo.findByEmail(email);
        model.addAttribute("studentName",res.getName());

        stnSrc.addAllActiveQuizes(email,model);
        stnSrc.addAttempedQuizData(email,model);
        stnSrc.findAllAnnouncements(email,model);
    }

    public void teacherHomePage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("teacherEmail");
        Teacher t = teacherRepo.GetByEmail(email);
        List<Quizes> record = quizRepo.findTheQuizesByTeacherId(t.getTeacherId());

        model.addAttribute("totalQuizes",record.size());

        long studentCount = roomRepo.countStudentsWithTeachers(t.getTeacherId());
        model.addAttribute("totalStudents",studentCount);

        int activeQuizesCount = 0;
        List<List<String>> recentQuizes = new ArrayList<>();

        for(Quizes q: record) {
            List<String> newRecord = new ArrayList<>();
            newRecord.add(q.getTitle());
            newRecord.add((""+q.getCreatedAt()).substring(0,10));
            Long count = (Long)QARepo.countTheAttendedQuizes(q.getQuizId());
            newRecord.add(""+count);

            if(q.getExpireAt().isAfter(LocalDateTime.now())) activeQuizesCount++;
            recentQuizes.add(newRecord);
        }

        model.addAttribute("activeQuizesCount",activeQuizesCount);
        recentQuizes.sort((a,b) -> {
            String x = a.get(1);
            String y = b.get(1);
            return y.compareTo(x);
        });

        model.addAttribute("recentQuizes",recentQuizes);
        roomSrc.putRoomsData(session,model);
    }
}
