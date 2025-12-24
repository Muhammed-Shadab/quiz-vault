package com.minProject.root.service;

import com.minProject.root.entity.*;
import com.minProject.root.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
public class StudentService {

    @Autowired
    private StudentRepository studentRepo;
    @Autowired
    private QuizAttemptRepository QARepo;
    @Autowired
    private AnnouncementStatusRepository annRepo;
    @Autowired
    private TeacherRepository teacherRepo;
    @Autowired
    private ActiveStatusRepository acRepo;
    @Autowired
    private RoomsRepository roomRepo;
    @Autowired
    private QuizesRepository quizRepo;

    public void addStudent(Student stn){
        Student s = studentRepo.save(stn);
        ActiveStatus a = new ActiveStatus();
        a.setStudent(s);
        a.setLastActiveTime(LocalDateTime.now());
        acRepo.save(a);
    }
    public boolean isStudentVerified(Student st, HttpSession session, RedirectAttributes redirectAttrs) {
        String email = st.getEmail();
        String password = st.getPassword();
        Student res = studentRepo.isStudentVerified(email, password);
        if(res == null) return false;
        return true;
    }

    public void findAllAnnouncements(String email,Model model) {
        Student s = studentRepo.findByEmail(email);
        List<Announcement> ann = annRepo.findAnnouncementsForSpecificStudent(s.getStudentId());

        List<List<String>> res = new ArrayList<>();
        for(Announcement a: ann) {
            List<String> tuples = new ArrayList<>();
            tuples.add(a.getTitle());
            tuples.add(a.getMessage());
            tuples.add(""+a.getCreatedAt().toLocalDate());
            tuples.add(a.getTeacher().getName());
            res.add(tuples);
        }
        res.sort((a,b) ->{
            String x = a.get(2);
            String y = b.get(2);
            return x.compareTo(y);
        });
        model.addAttribute("announcementsOfStudents",res);
    }

    public void updateActiveTime(HttpSession session, Map<String, Object> payload) {
        String studentEmail = (String) session.getAttribute("StudentEmail");
        Student s = studentRepo.findByEmail(studentEmail);
        long lastEvent = ((Number) payload.get("lastEvent")).longValue();

        Instant instant = Instant.ofEpochMilli(lastEvent);

        LocalDateTime lastEventDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        System.out.println(lastEventDateTime);
        acRepo.updateActiveTime(s.getStudentId(),lastEventDateTime);

    }

    public void addAllActiveQuizes(String email,Model model){
        Student res = studentRepo.findByEmail(email);
        List<String> roomsNames = roomRepo.findRoomsByStudent(res.getStudentId());
        List<List<String>> notifyQuizes = new ArrayList<>();

        for(String r:roomsNames) {
            List<Quizes> quizes = quizRepo.findQuizByRoomName(r);
            for(Quizes q: quizes){
                QuizAttempt status = QARepo.isQuizAttemptedByStudent(q.getQuizId(),res.getStudentId());
                if(status == null) {
                    List<String> tuple = new ArrayList<>();
                    tuple.add(r);
                    tuple.add(q.getTitle());
                    Duration duration = Duration.between(LocalDateTime.now(),q.getExpireAt());
                    tuple.add("Ends in "+duration.toHours() + " Hours");
                    tuple.add(q.getUrl().substring(20));
                    notifyQuizes.add(tuple);
                }
            }
        }

        notifyQuizes.sort((a,b) -> {
            String x = a.get(2);
            String y = b.get(2);

            if(x.length() > y.length()) return 1;
            else return x.compareTo(y);
        });
        model.addAttribute("notifyQuizes",notifyQuizes);
    }

    public void addAttempedQuizData(String email, Model model) {
        Student res = studentRepo.findByEmail(email);
        List<QuizAttempt> list = QARepo.findAllAttendedQuiz(res.getStudentId());

        List<List<String>> performance = new ArrayList<>();
        int bestScore = 0,avgScore = 0;
        for(QuizAttempt qa: list) {
            List<String> records = new ArrayList<>();
            Quizes q = qa.getQuizId();
            int correctQuestionsCount = qa.getCorrectQuestionsCount();
            int totalQuestions = q.getQuestionsCount();

            records.add(q.getTitle());
            int score = (correctQuestionsCount * 100) / totalQuestions;
            bestScore = Math.max(score,bestScore);
            avgScore += score;
            records.add(score+ "%");
            records.add(""+qa.getEnd_time().toLocalDate());

            performance.add(records);
        }
        model.addAttribute("bestScore",bestScore);
        if(performance.size() > 0) avgScore /= performance.size();
        model.addAttribute("avgScore",avgScore);
        performance.sort((a, b) -> {
            String x = a.get(2);
            String y = b.get(2);
            return y.compareTo(x);
        });
        model.addAttribute("performance", performance);

    }
}
