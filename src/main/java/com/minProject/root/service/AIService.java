package com.minProject.root.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minProject.root.entity.Question;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final WebClient webClient;

    public AIService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }
    public List<Question> generateQuiz(String extractedText,int count,String difficulty) throws JsonProcessingException {

        String prompt = """
            Generate """ + count + """ 
            multiple-choice questions of difficulty level """ + difficulty + """
            STRICT RULES:
            - Respond ONLY with raw JSON
            - NO ```json or ``` code blocks
            - NO extra commentary
            - NO explanations    
            Format:
            {
              "questions": [
                 {
                   "question": "...",
                   "options": ["A","B","C","D"],
                   "answer": "text of correct answer"
                 }
              ]
            }
            """ + extractedText;

        Map<String, Object> content = new HashMap<>();
        content.put("role", "user");
        content.put("parts", List.of(Map.of("text", prompt)));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(content));

        // API CALL
        String response = webClient.post()
                .uri(apiUrl + "?key=" + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);

        String result = root
                .path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();

        // CLEAN BACKTICKS
        String cleanJson = result
                .replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();

        JsonNode json = mapper.readTree(cleanJson);

        List<Question> questions = new ArrayList<>();
        for (JsonNode q : json.get("questions")) {

            Question newQ = new Question();
            newQ.setQuestion(q.get("question").asText());

            List<String> options = new ArrayList<>();
            for (JsonNode opt : q.get("options"))
                options.add(opt.asText());
            newQ.setOptions(options);

            newQ.setAnswer(q.get("answer").asText());

            questions.add(newQ);
        }

        return questions;
    }
}