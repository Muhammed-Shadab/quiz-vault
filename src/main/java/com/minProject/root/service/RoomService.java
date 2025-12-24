package com.minProject.root.service;

import com.minProject.root.entity.*;
import com.minProject.root.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class RoomService {
    @Autowired
    private TeacherRepository teacherRepo;
    @Autowired
    private RoomsRepository roomRepo;
    @Autowired
    private QuizesRepository quizRepo;
    @Autowired
    private QuizAttemptRepository QARepo;
    @Autowired
    private StudentRepository studentRepo;
    @Autowired
    private ActiveStatusRepository acRepo;


    public String addRoomsData(String roomTitle, HttpSession session, Model model) {
        String teacherEmail = (String) session.getAttribute("teacherEmail");
        if(teacherEmail == null) return "welcomePage";

        model.addAttribute("roomTitle", roomTitle);
        Teacher t = teacherRepo.GetByEmail(teacherEmail);

        List<Student> students = roomRepo.findStudentByRoomNameAndTeacher(roomTitle, t.getTeacherId());
        model.addAttribute("totalStudents", students.size());

        List<Quizes> quizes = quizRepo.findAllQuizByRoomName(roomTitle);
        quizes.sort((a, b) -> Long.compare(a.getQuizId(), b.getQuizId()));
        model.addAttribute("totalQuizes", quizes.size());

        List<List<Object>> studentsRecords = new ArrayList<>();  // Changed to Object

        for(Student s: students) {
            List<Object> tuples = new ArrayList<>();  // Changed to Object
            tuples.add(s.getName());
            tuples.add(s.getEmail());

            List<QuizAttempt> attendedQuiz = QARepo.findAllAttendedQuiz(s.getStudentId());
            attendedQuiz.sort((a, b) -> Long.compare(a.getQuizId().getQuizId(), b.getQuizId().getQuizId()));

            float average = 0;
            int count = 0, i = 0, j = 0;

            while(i < quizes.size() && j < attendedQuiz.size()) {
                Long x = quizes.get(i).getQuizId();
                Long y = attendedQuiz.get(j).getQuizId().getQuizId();

                if(x.equals(y)) {
                    count++;
                    average += ((float) attendedQuiz.get(j++).getCorrectQuestionsCount()) / quizes.get(i++).getQuestionsCount();
                } else if(x < y) {
                    i++;
                } else {
                    j++;
                }
            }
            if(count > 0) {
                average = average / count;
            } else {
                average = 0.0f;
            }

            tuples.add(count);
            tuples.add(average);
            LocalDateTime time = acRepo.isStudentACtive(s.getStudentId());
            if (Duration.between(time, LocalDateTime.now()).toSeconds() < 150) tuples.add(true);
            else tuples.add(false);
            studentsRecords.add(tuples);
        }
        model.addAttribute("studentsRecords", studentsRecords);

        List<List<Object>> quizesRecords = new ArrayList<>();

        for(Quizes q: quizes) {
            List<Object> tuple = new ArrayList<>();
            tuple.add(q.getTitle());
            tuple.add(q.getCreatedAt().toLocalDate());
            tuple.add(q.getExpireAt().toLocalDate());

            List<QuizAttempt> attemptedCount = QARepo.findTheAttendedQuizes(q.getQuizId());
            tuple.add(attemptedCount.size());

            int totalQuestions = q.getQuestionsCount();
            float averageScore = 0;
            for(QuizAttempt qa: attemptedCount) {
                averageScore += (((float) qa.getCorrectQuestionsCount()) /totalQuestions);
            }

            if(attemptedCount.size() > 0) averageScore = (((float) averageScore)/attemptedCount.size());

            tuple.add(averageScore);

            quizesRecords.add(tuple);
        }

        model.addAttribute("quizesRecords",quizesRecords);
        return "roomsPage";
    }

    public ResponseEntity<Map<String, Object>> addStudentInRoom(String roomName,String email,HttpSession session) {
            Map<String, Object> response = new HashMap<>();

            String teacherEmail = (String) session.getAttribute("teacherEmail");
            if(teacherEmail == null) {
                response.put("success", false);
                response.put("error", "Session expired. Please login again.");
                return ResponseEntity.status(401).body(response);
            }

            Student s = studentRepo.findByEmail(email);
            Teacher t = teacherRepo.GetByEmail(teacherEmail);

            // Case 1: Student doesn't exist
            if(s == null) {
                response.put("success", false);
                response.put("error", "Student with this email does not exist!");
                return ResponseEntity.badRequest().body(response);
            }

            // Case 2: Student already in room
            Rooms r = roomRepo.findByRoomAndStudent(roomName, s.getStudentId());
            if(r != null) {
                response.put("success", false);
                response.put("error", "Student is already in this room!");
                return ResponseEntity.badRequest().body(response);
            }

            // Case 3: Success
            r = new Rooms();
            r.setRoomName(roomName);
            r.setStudentId(s);
            r.setTeacherId(t);
            roomRepo.save(r);

            response.put("success", true);
            response.put("message", "Student added successfully!");
            return ResponseEntity.ok(response);
    }

    public void putRoomsData(HttpSession session, Model model) {
        String teacherEmail = (String) session.getAttribute("teacherEmail");

        Teacher t = teacherRepo.GetByEmail(teacherEmail);
        List<String> rooms = roomRepo.getGroupedRoomNames(t.getTeacherId());
        List<List<String>> RoomsRecord = new ArrayList<>();

        for(String r: rooms) {
            List<String> tuples = new ArrayList<>();
            tuples.add(r);
            tuples.add(""+roomRepo.CountStudentByRooms(r));
            tuples.add(""+quizRepo.countQuizesByRooms(r,t.getTeacherId()));
            RoomsRecord.add(tuples);
        }
        model.addAttribute("RoomsRecord",RoomsRecord);
    }

}
