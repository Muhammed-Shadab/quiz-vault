package com.minProject.root.controller;

import com.minProject.root.Task.FileTextExtractor;
import com.minProject.root.entity.Question;
import com.minProject.root.service.AIService;
import com.minProject.root.service.QuizesService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/Quiz")
public class QuizesController {

        private final com.minProject.root.service.AIService aisrc;
        private final FileTextExtractor extractor;
        @Autowired
        private QuizesService quizsrc;

        @Autowired
        public QuizesController(AIService aisrc, FileTextExtractor extractor) {
            this.aisrc = aisrc;
            this.extractor = extractor;
        }

        @PostMapping("/generateQuiz")
        public String generateQuiz(
                @RequestParam("title") String title,
                @RequestParam("description") String description,
                @RequestParam("questionsCount") int count,
                @RequestParam("duration") int duration,
                @RequestParam("marksOfEachQuestion") int marksOfEachQuestion,
                @RequestParam("difficulty") String difficulty,
                @RequestParam("roomName") String roomName,
                @RequestParam("ExpireAt") LocalDateTime expireAt,
                @RequestParam("file") MultipartFile file,
                HttpSession session,
                Model model) throws IOException {

                List<Question> questions = quizsrc.GenerateQuiz(title,description,count,duration,marksOfEachQuestion,difficulty,roomName,expireAt,file,session);
                model.addAttribute("questions",questions);
                return "generatedQuiz";

        }

        @PostMapping("/deleteQuestion/{index}")
        public ResponseEntity<Void> deleteQuestion(@PathVariable int index, HttpSession session) {
            return quizsrc.deleteQuestion(index,session);
        }


        @PostMapping("/addQuestion")
        @ResponseBody
        public Map<String,Object> addQuestion(
                @RequestParam String question,
                @RequestParam String option1,
                @RequestParam String option2,
                @RequestParam String option3,
                @RequestParam String option4,
                @RequestParam int correctIndex,
                HttpSession session,
                Model model) {

            return quizsrc.addQuestion(question,option1,option2,option3,option4,correctIndex,session,model);

        }

        @PostMapping("/activateQuiz")
        @ResponseBody
        public String activateQuiz(HttpSession session) {
            return quizsrc.activateQuiz(session);
        }

        @GetMapping("/{url}")
        public String renderQuizPage(@PathVariable String url,Model model,HttpSession session) {
            if(session.getAttribute("StudentEmail") == null) return "welcomePage";
            String response = quizsrc.renderQuizPage(url,model,session);
            if(response != null) return "quiz";
            else return "The quiz is expired";
        }

        @PostMapping("/countMarks")
        public String countMarks(@RequestParam Map<String,String> selectedOptions,HttpSession session) {
            int marks = quizsrc.countMarks(selectedOptions,session);
            if(marks == -1) return "welcomePage";
            return "succesfully";
        }

}

