package com.minProject.root.service;


import com.minProject.root.entity.Quizes;
import com.minProject.root.entity.Teacher;
import com.minProject.root.repository.QuizAttemptRepository;
import com.minProject.root.repository.QuizesRepository;
import com.minProject.root.repository.RoomsRepository;
import com.minProject.root.repository.TeacherRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepo;
    @Autowired
    private RoomsRepository roomRepo;
    @Autowired
    private QuizesRepository quizRepo;
    @Autowired
    private QuizAttemptRepository QARepo;

    public boolean addTeacher(Teacher t){
        if(teacherRepo.isEmailExists(t.getEmail()).isPresent()) return false;
        teacherRepo.save(t);
        return true;
    }

    public boolean isTeacherVerified(Teacher t) {
        String email = t.getEmail();
        String password = t.getPassword();
        Optional<Teacher> res = teacherRepo.isTeacherVerified(email,password);
        if(!res.isPresent()) return false;
        return true;
    }

    public void teacherProfile(String teacherEmail, Model model) {
        Teacher t = teacherRepo.GetByEmail(teacherEmail);
        model.addAttribute("teacherName",t.getName());
        model.addAttribute("teacherEmail",teacherEmail);
        model.addAttribute("teacherPhoneNumber",t.getPhoneNo());

        long roomsCount = (roomRepo.getGroupedRoomNames(t.getTeacherId())).size();
        model.addAttribute("roomsCount",roomsCount);

        List<Quizes> quizes = quizRepo.findTheQuizesByTeacherId(t.getTeacherId());
        model.addAttribute("QuizesCount",quizes.size());

        long studentCount = roomRepo.countStudentsWithTeachers(t.getTeacherId());
        model.addAttribute("studentsCount",studentCount);

        long quizAttemptCount = 0;

        for(Quizes q: quizes) {
            quizAttemptCount = (((long)quizAttemptCount) + QARepo.countTheAttendedQuizes(q.getQuizId()));
        }
        System.out.println(quizAttemptCount);
        model.addAttribute("quizAttemptCount",quizAttemptCount);
    }

    public String recentQuizes(HttpSession session, Model model) {
        String teacherEmail = (String) session.getAttribute("teacherEmail");

        if(teacherEmail == null) return "welcome";
        Teacher t =teacherRepo.GetByEmail(teacherEmail);


        List<Quizes> record = quizRepo.findTheQuizesByTeacherId(t.getTeacherId());
        List<List<Object>> recentQuizes = new ArrayList<>();

        for(Quizes q: record) {
            List<Object> newRecord = new ArrayList<>();
            newRecord.add(q.getTitle());
            newRecord.add((""+q.getCreatedAt()).substring(0,10));
            newRecord.add(("" + q.getExpireAt()).substring(0,10));
            Long count = (Long)QARepo.countTheAttendedQuizes(q.getQuizId());
            newRecord.add(""+count);

            if(q.getExpireAt().isAfter(LocalDateTime.now())) newRecord.add(true);
            else newRecord.add(false);

            recentQuizes.add(newRecord);
        }

        recentQuizes.sort((a,b) -> {
            String x = "" + a.get(1);
            String y = "" + b.get(1);
            return y.compareTo(x);
        });

        model.addAttribute("recentQuizes",recentQuizes);
        return "recentQuizPage";
    }
}
