package com.minProject.root.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;

@Data
@NoArgsConstructor
@Entity
public class Quizes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long QuizId;

    private String title;
    private String description;
    private int questionsCount;
    private int duration;
    private int marksOfEachQuestion;
    private String difficulty;
    private String roomName;
    private LocalDateTime expireAt;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<Question> questions;

    private String url;

}
