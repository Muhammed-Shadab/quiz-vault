package com.minProject.root.service;

import com.minProject.root.Task.FileTextExtractor;
import com.minProject.root.entity.Question;
import com.minProject.root.entity.QuizAttempt;
import com.minProject.root.entity.Quizes;
import com.minProject.root.entity.Student;
import com.minProject.root.repository.QuizAttemptRepository;
import com.minProject.root.repository.QuizesRepository;
import com.minProject.root.repository.StudentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class QuizesService {

    @Autowired
    private QuizesRepository quizRepo;
    @Autowired
    private QuizAttemptRepository QARepo;
    @Autowired
    private StudentRepository stnRepo;

    private final com.minProject.root.service.AIService aisrc;
    private final FileTextExtractor extractor;

    @Autowired
    public QuizesService(AIService aisrc, FileTextExtractor extractor) {
        this.aisrc = aisrc;
        this.extractor = extractor;
    }

    public List<Question> GenerateQuiz(String title, String description, int count, int duration,int marksOfEachQuestion,
                                       String difficulty, String roomName, LocalDateTime expiresAt,MultipartFile file, HttpSession session) throws IOException {

        String text = extractor.extractText(file.getInputStream());
        List<Question> questions = new ArrayList<>(aisrc.generateQuiz(text,count,difficulty));

        session.setAttribute("title",title);
        session.setAttribute("description",description);
        session.setAttribute("questionsCount",count);
        session.setAttribute("duration",duration);
        session.setAttribute("marksOfEachQuestion",marksOfEachQuestion);
        session.setAttribute("difficulty",difficulty);
        session.setAttribute("roomName",roomName);
        session.setAttribute("expiresAt",expiresAt);
        session.setAttribute("questions",questions);

        return questions;
    }

    public ResponseEntity<Void> deleteQuestion(int index, HttpSession session) {
        List<Question> questions = (List<Question>) session.getAttribute("questions");
        if (index >= 0 && index < questions.size()) {
            questions.remove(index);
        }
        return ResponseEntity.ok().build();
    }

    public Map<String,Object> addQuestion(String question, String option1, String option2,String option3,
                                          String option4, int correctIndex, HttpSession session,Model model) {

        Question newQuestion = new Question();
        newQuestion.setQuestion(question);
        newQuestion.setOptions(List.of(option1, option2, option3, option4));
        newQuestion.setAnswer(List.of(option1, option2, option3, option4).get(correctIndex - 1));
        List<Question> questions = (List<Question>) session.getAttribute("questions");
        questions.add(newQuestion);
        model.addAttribute("questions", questions);

        Map<String,Object> response = new HashMap<>();
        response.put("question",question);
        response.put("options",newQuestion.getOptions());
        return response;
    }


    public String activateQuiz(HttpSession session) {
        String title = (String) session.getAttribute("title");
        String description = (String) session.getAttribute("description");
        int questionsCount = (int) session.getAttribute("questionsCount");
        int duration = (int) session.getAttribute("duration");
        int marksOfEachQuestion = (int) session.getAttribute("marksOfEachQuestion");
        String difficulty = (String) session.getAttribute("difficulty");
        String roomName = (String) session.getAttribute("roomName");
        LocalDateTime expiresAt = (LocalDateTime) session.getAttribute("expiresAt");
        List<Question> questions = (List<Question>) session.getAttribute("questions");

        if (questions == null || title == null) return "createQuiz";

        String url = "localhost:8080/Quiz/" + UUID.randomUUID();

        Quizes newQuiz = new Quizes();
        newQuiz.setTitle(title);
        newQuiz.setDescription(description);
        newQuiz.setQuestionsCount(questionsCount);
        newQuiz.setDuration(duration);
        newQuiz.setMarksOfEachQuestion(marksOfEachQuestion);
        newQuiz.setDifficulty(difficulty);
        newQuiz.setRoomName(roomName);
        newQuiz.setQuestions(questions);
        newQuiz.setUrl(url);
        newQuiz.setExpireAt(expiresAt);

        quizRepo.save(newQuiz);
        return url;
    }

    public String renderQuizPage(String url,Model model,HttpSession session) {
        url = "localhost:8080/Quiz/" + url;
        Quizes q = quizRepo.findByUrl(url);
        LocalDateTime now = LocalDateTime.now();

        if(q != null && q.getExpireAt().isAfter(now)) {
            List<Question> questions = q.getQuestions();
            model.addAttribute("questions",questions);
            model.addAttribute("title",q.getTitle());
            model.addAttribute("id",q.getQuizId());
            session.setAttribute("quizId",q.getQuizId());
            session.setAttribute("startTime",LocalDateTime.now());
            return "quiz";
        }else return null;
    }

    public int countMarks(Map<String, String> selectedOptions, HttpSession session) {
        Long QuizId = (Long) session.getAttribute("quizId");
        String studentEmail = (String) session.getAttribute("StudentEmail");
        if(QuizId == null || studentEmail == null) return -1;

        Optional<Quizes> optionalQuiz = quizRepo.findById(QuizId);
        Quizes q = optionalQuiz.get();

        Optional<Student> optionalStudent = stnRepo.findByEmail(studentEmail);
        Student s = optionalStudent.get();

        List<Question> questions = q.getQuestions();

        int score = 0;
        for(String key: selectedOptions.keySet()) {
            int idx = Integer.parseInt(key) - 1;
            Question temp = questions.get(idx);
            if(selectedOptions.get(key).equals(temp.getAnswer())) score++;
        }
        QuizAttempt newRecord = new QuizAttempt();
        newRecord.setScore(score*q.getMarksOfEachQuestion());
        newRecord.setCorrectQuestionsCount(score);
        newRecord.setEnd_time(LocalDateTime.now());
        newRecord.setStart_time(((LocalDateTime)session.getAttribute("startTime")));
        newRecord.setQuiz(q);
        newRecord.setStudent(s);


        QARepo.save(newRecord);

        return score*q.getMarksOfEachQuestion();

    }
}
