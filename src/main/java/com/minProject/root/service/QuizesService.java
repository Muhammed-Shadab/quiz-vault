package com.minProject.root.service;

import com.minProject.root.Task.FileTextExtractor;
import com.minProject.root.entity.*;
import com.minProject.root.repository.*;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.collections4.bag.SynchronizedSortedBag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
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
    @Autowired
    private TeacherRepository teacherRepo;
    @Autowired
    private RoomsRepository roomRepo;
    @Autowired
    private pageControllerService pageControllerSrc;


    private final com.minProject.root.service.AIService aisrc;
    private final FileTextExtractor extractor;

    @Autowired
    public QuizesService(AIService aisrc, FileTextExtractor extractor) {
        this.aisrc = aisrc;
        this.extractor = extractor;
    }

    public List<Question> GenerateQuiz(String title, String description, int count, int duration,int marksOfEachQuestion,
                                       String difficulty, String roomName, LocalDateTime expiresAt,int maxTabSwitches,MultipartFile file, HttpSession session) throws IOException {


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
        session.setAttribute("maxTabSwitches",maxTabSwitches);

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
        String teacherEmail = (String) session.getAttribute("teacherEmail");
        if(teacherEmail == null) return "welcome";

        String title = (String) session.getAttribute("title");
        String description = (String) session.getAttribute("description");
        int questionsCount = (int) session.getAttribute("questionsCount");
        int duration = (int) session.getAttribute("duration");
        int marksOfEachQuestion = (int) session.getAttribute("marksOfEachQuestion");
        String difficulty = (String) session.getAttribute("difficulty");
        String roomName = (String) session.getAttribute("roomName");
        LocalDateTime expiresAt = (LocalDateTime) session.getAttribute("expiresAt");
        int maxTabSwitches = (int) session.getAttribute("maxTabSwitches");
        List<Question> questions = (List<Question>) session.getAttribute("questions");

        if (questions == null || title == null) return "createQuiz";

        String url = "localhost:8080/Quiz/" + UUID.randomUUID();
        Teacher t = teacherRepo.GetByEmail(teacherEmail);


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
        newQuiz.setTeacherId(t);
        newQuiz.setCreatedAt(LocalDateTime.now());
        newQuiz.setMaxTabSwitches(maxTabSwitches);

        quizRepo.save(newQuiz);
        return url;
    }

    public String renderQuizPage(String url,Model model,HttpSession session) {
        String studentEmail = (String)session.getAttribute("StudentEmail");
        if(studentEmail == null) return "welcomePage";
        Student s = stnRepo.findByEmail(studentEmail);
        Quizes q = quizRepo.findByUrl(url);
        LocalDateTime now = LocalDateTime.now();

        if(q != null && q.getExpireAt().isAfter(now)) {
            List<Question> questions = q.getQuestions();
            model.addAttribute("questions",questions);
            model.addAttribute("title",q.getTitle());
            model.addAttribute("id",q.getQuizId());
            model.addAttribute("expireAt",q.getDuration());
            session.setAttribute("quizId",q.getQuizId());

            Teacher t = q.getTeacherId();
            String roomName = q.getRoomName();

            Rooms r = roomRepo.findByRoomAndStudent(roomName,s.getStudentId());
            if(r == null) {
                Rooms newRoom = new Rooms();
                newRoom.setStudentId(s);
                newRoom.setTeacherId(t);
                newRoom.setRoomName(roomName);
                roomRepo.save(newRoom);
            }
            QuizAttempt attempt = QARepo.isQuizAttemptedByStudent(q.getQuizId(),s.getStudentId());
            if(attempt == null) {

                QuizAttempt quiz = new QuizAttempt();
                quiz.setQuizId(q);
                quiz.setStart_time(LocalDateTime.now());
                quiz.setStudentId(s);
                quiz.setScore(0);
                quiz.setEnd_time(null);
                quiz.setCorrectQuestionsCount(0);
                quiz.setTabSwitchingCount(0);
                QuizAttempt qa = QARepo.save(quiz);
                session.setAttribute("QuizAttemptId",qa.getQAId());
                return "quiz";
            }
            else {
                addAttemptedQuizData(attempt,model);
                return "quizAlreadyAttempted";
            }
        }else return "The quiz Is expired";
    }

    public String countMarks(Map<String, String> selectedOptions, HttpSession session,Model model) {
        Long QuizId = (Long) session.getAttribute("quizId");
        String studentEmail = (String) session.getAttribute("StudentEmail");
        if(QuizId == null || studentEmail == null) return "welcomePage";
        Student s = stnRepo.findByEmail(studentEmail);

        QuizAttempt qa = QARepo.isQuizAttemptedByStudent(QuizId,s.getStudentId());

        if(qa.getEnd_time() == null) {
            Quizes q = qa.getQuizId();
            LocalDateTime expectedTime = qa.getStart_time().plusMinutes(q.getDuration());
            expectedTime = expectedTime.plusSeconds(15);

            if (qa.getTabSwitchingCount() <= q.getMaxTabSwitches() && q.getExpireAt().isAfter(LocalDateTime.now())
                && LocalDateTime.now().isBefore(expectedTime)) {

                List<Question> questions = q.getQuestions();

                int score = 0;
                for (String key : selectedOptions.keySet()) {
                    int idx = Integer.parseInt(key) - 1;
                    Question temp = questions.get(idx);
                    System.out.println(selectedOptions.get(key) + " " + temp.getAnswer());
                    if (selectedOptions.get(key).equals(temp.getAnswer())) score++;
                }

                qa.setCorrectQuestionsCount(score);
                qa.setScore(score * q.getMarksOfEachQuestion());
            }
            qa.setEnd_time(LocalDateTime.now());
            QARepo.save(qa);
        }
        addAttemptedQuizData(qa, model);
        return "quizAlreadyAttempted";

    }

    private void addAttemptedQuizData(QuizAttempt attempt, Model model) {
        Quizes q = attempt.getQuizId();
        model.addAttribute("quizTitle",q.getTitle());
        int score = ((attempt.getCorrectQuestionsCount()*100)/q.getQuestionsCount());
        model.addAttribute("score",score);
        model.addAttribute("correctAnswers",attempt.getCorrectQuestionsCount());
        model.addAttribute("totalQuestions",q.getQuestionsCount());
        model.addAttribute("attemptedDate",attempt.getEnd_time().toLocalDate());

    }

    public String tabSwitchingDetected(HttpSession session, Model model) {
        String studentEmail = (String) session.getAttribute("StudentEmail");
        Student s = stnRepo.findByEmail(studentEmail);
        Long quizId = (Long) session.getAttribute("QuizAttemptId");
        QARepo.updateTabSwitching(quizId);
        Integer tabSwitchingCount = QARepo.findTabSwitchingCount(quizId);

        if(tabSwitchingCount >= 3){
            QARepo.setscore(0,0,LocalDateTime.now(),quizId);
            return "yes";
        }
        return "no";

    }
}
